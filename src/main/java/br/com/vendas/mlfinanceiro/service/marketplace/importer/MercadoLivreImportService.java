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
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoLivreImportService {

    private final MercadoLivreClient mercadoLivreClient;
    private final SaleRepository saleRepository;

    public MercadoLivreImportService(
            MercadoLivreClient mercadoLivreClient,
            SaleRepository saleRepository
    ) {
        this.mercadoLivreClient = mercadoLivreClient;
        this.saleRepository = saleRepository;
    }

    // Mantido retornando 'int' para resolver o erro do MercadoLivreController de imediato
    public int importOrders() {
        return importAndReturnSales().size();
    }

    public List<Sale> importAndReturnSales() {
        List<Sale> savedSales = new ArrayList<>();

        System.out.println("=== INICIANDO IMPORTAÇÃO MERCADO LIVRE ===");

        MercadoLivreOrderResponse response = mercadoLivreClient.getOrders();
        if (response == null || response.getResults() == null) {
            return savedSales;
        }

        for (MercadoLivreOrderResult order : response.getResults()) {
            String orderId = String.valueOf(order.getId());

            if (saleRepository.existsByOrderId(orderId)) {
                System.out.println("Pedido já importado anteriormente: " + orderId);
                continue;
            }

            MercadoLivreOrderDetail detail = mercadoLivreClient.getOrderById(order.getId());
            if (detail == null || detail.getOrderItems() == null || detail.getOrderItems().isEmpty()) {
                continue;
            }

            MercadoLivreOrderItem firstItem = detail.getOrderItems().get(0);
            MercadoLivrePayment payment = detail.getPayments() != null && !detail.getPayments().isEmpty()
                    ? detail.getPayments().get(0)
                    : null;

            // --- CÁLCULOS FINANCEIROS CORRIGIDOS ---
            BigDecimal unitPrice = firstItem.getUnitPrice() != null ? firstItem.getUnitPrice() : BigDecimal.ZERO;
            Integer quantity = firstItem.getQuantity() != null ? firstItem.getQuantity() : 1;
            BigDecimal grossAmount = unitPrice.multiply(new BigDecimal(quantity));

            // Captura da Tarifa Comercial (Comissão ML)
            BigDecimal fee = BigDecimal.ZERO;

            if (payment != null
                    && payment.getMarketplaceFee() != null
                    && payment.getMarketplaceFee().compareTo(BigDecimal.ZERO) > 0) {

                fee = payment.getMarketplaceFee();

            } else if (firstItem.getSaleFee() != null) {

                fee = firstItem.getSaleFee()
                        .multiply(new BigDecimal(quantity));
            }

            BigDecimal shippingCost = BigDecimal.ZERO;

            Long shippingId =
                    detail.getShipping() != null
                            ? detail.getShipping().getId()
                            : null;

            if (shippingId != null) {

                try {

                    MercadoLivreShipmentResponse shipmentData =
                            mercadoLivreClient.getShipmentById(shippingId);

                    if (shipmentData != null) {

                        if (shipmentData.getShippingOption() != null
                                && shipmentData.getShippingOption().getListCost() != null) {

                            shippingCost =
                                    shipmentData.getShippingOption().getListCost();

                        } else if (shipmentData.getBaseCost() != null) {

                            shippingCost = shipmentData.getBaseCost();

                        } else if (
                                shipmentData.getCosts() != null
                                        && shipmentData.getCosts().getSenderCost() != null) {

                            shippingCost =
                                    shipmentData.getCosts().getSenderCost();
                        }
                    }

                } catch (Exception e) {

                    System.err.println(
                            "Erro ao processar custo logístico do envio "
                                    + shippingId
                                    + ": "
                                    + e.getMessage()
                    );
                }
            }

            // Define o valor recebido na conta líquida do Mercado Pago
            BigDecimal netAmount = (payment != null && payment.getNetReceivedAmount() != null)
                    ? payment.getNetReceivedAmount()
                    : grossAmount.subtract(fee).subtract(shippingCost);

            System.out.println("\n========== PEDIDO ==========");
            System.out.println("Order: " + orderId);
            System.out.println("GrossAmount: " + grossAmount);

            if (payment != null) {
                System.out.println("PaymentId: " + payment.getId());
                System.out.println("TransactionAmount: " + payment.getTransactionAmount());
                System.out.println("TotalPaidAmount: " + payment.getTotal_paid_amount());
                System.out.println("MarketplaceFee: " + payment.getMarketplaceFee());
                System.out.println("NetReceivedAmount: " + payment.getNetReceivedAmount());
            } else {
                System.out.println("Payment: NULL");
            }

            System.out.println("ShippingCost: " + shippingCost);
            System.out.println("CalculatedNetAmount: " + netAmount);
            System.out.println("============================\n");

            // Instanciação e persistência do domínio Sale
            Sale sale = new Sale();
            sale.setOrderId(orderId);
            sale.setMarketplace(Marketplace.MERCADO_LIVRE);
            sale.setProductName(firstItem.getItem().getTitle());
            sale.setSku(firstItem.getItem().getId());
            sale.setMarketplaceItemId(
                    firstItem.getItem().getId()
            );
            sale.setQuantity(quantity);
            sale.setSoldAt(LocalDateTime.now());

            // Mapeamento correto de cada ramificação do dinheiro
            sale.setUnitSalePrice(unitPrice);
            sale.setGrossAmount(grossAmount);
            sale.setMarketplaceFee(fee);
            sale.setShippingCost(shippingCost);
            sale.setNetAmount(netAmount);

            // Custos locais/internos do seu controle de estoque
            sale.setExtraCosts(BigDecimal.ZERO);
            sale.setProductCost(BigDecimal.ZERO);

            // Executa a lógica de margem interna baseada em dados reais
            sale.calculateProfit();

            System.out.println("Salvando venda com sucesso. ID Pedido: " + orderId + " | Líquido: R$ " + netAmount);
            Sale saved = saleRepository.save(sale);
            savedSales.add(saved);
        }

        System.out.println("Importação finalizada. Total de novos registros: " + savedSales.size());
        return savedSales;
    }
}