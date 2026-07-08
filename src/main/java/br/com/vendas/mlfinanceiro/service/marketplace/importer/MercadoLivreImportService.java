package br.com.vendas.mlfinanceiro.service.marketplace.importer;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.MercadoLivreClient;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResponse;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResult;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreOrderDetail;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreOrderItem;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivrePayment;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreShipmentResponse;
import br.com.vendas.mlfinanceiro.repository.SaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.repository.ProductRepository;
import br.com.vendas.mlfinanceiro.domain.StockMovement;
import br.com.vendas.mlfinanceiro.domain.StockMovementType;
import br.com.vendas.mlfinanceiro.repository.StockMovementRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoLivreImportService {

    // Instanciando o Logger para a classe
    private static final Logger log = LoggerFactory.getLogger(MercadoLivreImportService.class);

    private final MercadoLivreClient mercadoLivreClient;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    public MercadoLivreImportService(
            MercadoLivreClient mercadoLivreClient,
            SaleRepository saleRepository,
            ProductRepository productRepository,
            StockMovementRepository stockMovementRepository
    ) {
        this.mercadoLivreClient = mercadoLivreClient;
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    public int importOrders() {
        return importAndReturnSales().size();
    }

    public List<Sale> importAndReturnSales() {
        List<Sale> savedSales = new ArrayList<>();
        log.info("=== INICIANDO IMPORTAÇÃO MERCADO LIVRE ===");

        int offset = 0;
        int limit = 50;
        boolean hasMore = true;

        while (hasMore) {
            MercadoLivreOrderResponse response = mercadoLivreClient.getOrders(offset, limit);
            logPageStatus(offset, response);

            if (isResponseEmpty(response)) {
                break;
            }

            for (MercadoLivreOrderResult order : response.getResults()) {
                processSingleOrder(order, savedSales);
            }

            offset += limit;
            hasMore = response.getPaging() != null && offset < response.getPaging().getTotal();
            logPageLoaded(offset, response);
        }

        log.info("Importação finalizada. Total de novos registros: {}", savedSales.size());
        return savedSales;
    }

    private boolean isResponseEmpty(MercadoLivreOrderResponse response) {
        return response == null || response.getResults() == null || response.getResults().isEmpty();
    }

    private void processSingleOrder(MercadoLivreOrderResult order, List<Sale> savedSales) {
        String orderId = String.valueOf(order.getId());

        if (saleRepository.existsByOrderId(orderId)) {
            log.info("Pedido já importado anteriormente: {}", orderId);
            return;
        }

        MercadoLivreOrderDetail detail = mercadoLivreClient.getOrderById(order.getId());
        if (detail == null || detail.getOrderItems() == null || detail.getOrderItems().isEmpty()) {
            return;
        }

        MercadoLivreOrderItem firstItem = detail.getOrderItems().get(0);
        String sellerSku = sanitizeSku(firstItem.getItem().getSellerSku());
        MercadoLivrePayment payment = getFirstPayment(detail);

        int quantity = firstItem.getQuantity() != null ? firstItem.getQuantity() : 1;
        BigDecimal unitPrice = firstItem.getUnitPrice() != null ? firstItem.getUnitPrice() : BigDecimal.ZERO;
        BigDecimal grossAmount = unitPrice.multiply(new BigDecimal(quantity));

        BigDecimal fee = calculateFee(payment, firstItem, quantity);
        BigDecimal shippingCost = calculateShippingCost(detail);
        BigDecimal netAmount = calculateNetAmount(payment, grossAmount, fee, shippingCost);

        logOrderFinancials(orderId, grossAmount, payment, shippingCost, netAmount);

        Product product = updateStockAndInventory(sellerSku, quantity, orderId);

        // O mapeamento da entidade Sale foi incorporado aqui, eliminando o método de 10 parâmetros
        Sale sale = new Sale();
        sale.setOrderId(orderId);
        sale.setMarketplace(Marketplace.MERCADO_LIVRE);
        sale.setProductName(firstItem.getItem().getTitle());
        sale.setSku(sellerSku);
        sale.setMarketplaceItemId(firstItem.getItem().getId());
        sale.setQuantity(quantity);
        sale.setProductCost((product != null && product.getCostPrice() != null) ? product.getCostPrice() : BigDecimal.ZERO);
        sale.setSoldAt(parseSoldAt(detail.getDateClosed()));
        sale.setUnitSalePrice(unitPrice);
        sale.setGrossAmount(grossAmount);
        sale.setMarketplaceFee(fee);
        sale.setShippingCost(shippingCost);
        sale.setNetAmount(netAmount);
        sale.setExtraCosts(BigDecimal.ZERO);
        sale.calculateProfit();

        savedSales.add(saleRepository.save(sale));
        log.info("Salvando venda com sucesso. ID Pedido: {} | Líquido: R$ {}", orderId, netAmount);
    }

    private String sanitizeSku(String sku) {
        if (sku != null && sku.trim().isEmpty()) {
            return null;
        }
        return sku;
    }

    private MercadoLivrePayment getFirstPayment(MercadoLivreOrderDetail detail) {
        return detail.getPayments() != null && !detail.getPayments().isEmpty()
                ? detail.getPayments().get(0)
                : null;
    }

    private BigDecimal calculateFee(MercadoLivrePayment payment, MercadoLivreOrderItem firstItem, int quantity) {
        if (payment != null && payment.getMarketplaceFee() != null && payment.getMarketplaceFee().compareTo(BigDecimal.ZERO) > 0) {
            return payment.getMarketplaceFee();
        }
        if (firstItem.getSaleFee() != null) {
            return firstItem.getSaleFee().multiply(new BigDecimal(quantity));
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateShippingCost(MercadoLivreOrderDetail detail) {
        if (detail.getShipping() == null || detail.getShipping().getId() == null) {
            return BigDecimal.ZERO;
        }
        Long shippingId = detail.getShipping().getId();
        try {
            MercadoLivreShipmentResponse shipmentData = mercadoLivreClient.getShipmentById(shippingId);
            return extractCostFromShipment(shipmentData);
        } catch (Exception e) {
            log.error("Erro ao processar custo logístico do envio {}: {}", shippingId, e.getMessage(), e);
            return BigDecimal.ZERO;
        }
    }

    private BigDecimal extractCostFromShipment(MercadoLivreShipmentResponse shipmentData) {
        if (shipmentData == null) {
            return BigDecimal.ZERO;
        }
        if (shipmentData.getShippingOption() != null && shipmentData.getShippingOption().getListCost() != null) {
            return shipmentData.getShippingOption().getListCost();
        }
        if (shipmentData.getBaseCost() != null) {
            return shipmentData.getBaseCost();
        }
        if (shipmentData.getCosts() != null && shipmentData.getCosts().getSenderCost() != null) {
            return shipmentData.getCosts().getSenderCost();
        }
        return BigDecimal.ZERO;
    }

    private BigDecimal calculateNetAmount(MercadoLivrePayment payment, BigDecimal grossAmount, BigDecimal fee, BigDecimal shippingCost) {
        return (payment != null && payment.getNetReceivedAmount() != null)
                ? payment.getNetReceivedAmount()
                : grossAmount.subtract(fee).subtract(shippingCost);
    }

    private Product updateStockAndInventory(String sellerSku, int quantity, String orderId) {
        if (sellerSku == null) {
            return null;
        }
        Product product = productRepository.findBySku(sellerSku).orElse(null);
        if (product != null) {
            product.setStockQuantity(product.getStockQuantity() - quantity);
            productRepository.save(product);

            StockMovement movement = new StockMovement();
            movement.setSku(sellerSku);
            movement.setType(StockMovementType.SALE);
            movement.setQuantity(quantity);
            movement.setReference(orderId);
            stockMovementRepository.save(movement);
        }
        return product;
    }

    private LocalDateTime parseSoldAt(String dateClosed) {
        if (dateClosed != null) {
            return OffsetDateTime.parse(dateClosed)
                    .atZoneSameInstant(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        return LocalDateTime.now();
    }

    private void logPageStatus(int offset, MercadoLivreOrderResponse response) {
        int pedidos = (response != null && response.getResults() != null) ? response.getResults().size() : 0;
        log.info("Página offset={} | pedidos={}", offset, pedidos);
    }

    private void logPageLoaded(int offset, MercadoLivreOrderResponse response) {
        int total = (response != null && response.getPaging() != null) ? response.getPaging().getTotal() : 0;
        boolean hasMore = (response != null && response.getPaging() != null && offset < response.getPaging().getTotal());
        log.info("Página carregada | offset={} de {} | hasMore={}", offset, total, hasMore);
    }

    private void logOrderFinancials(String orderId, BigDecimal grossAmount, MercadoLivrePayment payment, BigDecimal shippingCost, BigDecimal netAmount) {
        log.info("\n========== PEDIDO ==========");
        log.info("Order: {}", orderId);
        log.info("GrossAmount: {}", grossAmount);

        if (payment != null) {
            log.info("PaymentId: {}", payment.getId());
            log.info("TransactionAmount: {}", payment.getTransactionAmount());
            log.info("TotalPaidAmount: {}", payment.getTotal_paid_amount());
            log.info("MarketplaceFee: {}", payment.getMarketplaceFee());
            log.info("NetReceivedAmount: {}", payment.getNetReceivedAmount());
        } else {
            log.info("Payment: NULL");
        }

        log.info("ShippingCost: {}", shippingCost);
        log.info("CalculatedNetAmount: {}", netAmount);
        log.info("============================\n");
    }
}