package br.com.vendas.mlfinanceiro.service.marketplace.importer;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.integration.shopee.ShopeeClient;
import br.com.vendas.mlfinanceiro.repository.ProductRepository;
import br.com.vendas.mlfinanceiro.repository.SaleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.domain.StockMovement;
import br.com.vendas.mlfinanceiro.domain.StockMovementType;
import br.com.vendas.mlfinanceiro.repository.StockMovementRepository;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ShopeeImportService {

    private static final Logger logger = LoggerFactory.getLogger(ShopeeImportService.class);

    // CONSTANTES
    private static final String RESPONSE_KEY = "response";
    private static final String ITEM_LIST_KEY = "item_list";

    private final ShopeeClient shopeeClient;
    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final StockMovementRepository stockMovementRepository;

    public ShopeeImportService(
            ShopeeClient shopeeClient,
            SaleRepository saleRepository,
            ProductRepository productRepository,
            StockMovementRepository stockMovementRepository
    ) {
        this.shopeeClient = shopeeClient;
        this.saleRepository = saleRepository;
        this.productRepository = productRepository;
        this.stockMovementRepository = stockMovementRepository;
    }

    /**
     * Fluxo limpo eliminando validações redundantes que geravam o erro no Sonar.
     */
    public int importOrders() {
        int importedCount = 0;
        String cursor = null;
        boolean hasMore = true;
        int pagesRead = 0;

        while (hasMore && pagesRead < 100) {
            JsonNode orderListResponse = shopeeClient.getOrderList(cursor);
            if (orderListResponse == null) {
                break;
            }

            JsonNode response = orderListResponse.get(RESPONSE_KEY);
            if (response == null) {
                break;
            }

            JsonNode orders = response.get("order_list");
            if (orders == null || !orders.isArray()) {
                break;
            }

            importedCount += processOrdersPage(orders);

            hasMore = response.path("more").asBoolean(false);
            cursor = response.path("next_cursor").asText(null);
            pagesRead++;
        }

        return importedCount;
    }

    private int processOrdersPage(JsonNode orders) {
        int count = 0;
        for (JsonNode order : orders) {
            String orderSn = order.path("order_sn").asText();
            if (isOrderEligibleForImport(orderSn) && importOrderDetail(orderSn)) {
                count++;
            }
        }
        return count;
    }

    private boolean isOrderEligibleForImport(String orderSn) {
        return !orderSn.trim().isEmpty() && !saleRepository.existsByOrderId(orderSn);
    }

    private boolean importOrderDetail(String orderSn) {
        JsonNode detail = fetchOrderDetail(orderSn);
        if (detail == null) {
            return false;
        }

        JsonNode firstItem = getFirstItem(detail);
        if (firstItem == null) {
            return false;
        }

        JsonNode escrowResponse = shopeeClient.getEscrowDetail(orderSn);
        JsonNode orderIncome = escrowResponse == null ? null : escrowResponse.path(RESPONSE_KEY).path("order_income");

        processOrderBillingAndInventory(orderSn, detail, firstItem, orderIncome);
        return true;
    }

    private JsonNode fetchOrderDetail(String orderSn) {
        JsonNode detailResponse = shopeeClient.getOrderDetail(orderSn);
        JsonNode orders = detailResponse == null ? null : detailResponse.path(RESPONSE_KEY).path("order_list");

        // Alterado de .isEmpty() para .size() == 0 para evitar erros de compilação por versão do Jackson
        if (orders == null || !orders.isArray() || orders.size() == 0) {
            return null;
        }
        return orders.get(0);
    }

    private JsonNode getFirstItem(JsonNode detail) {
        JsonNode itemList = detail.path(ITEM_LIST_KEY);

        // Alterado de !itemList.isEmpty() para .size() > 0 para compatibilidade universal
        if (itemList.isArray() && itemList.size() > 0) {
            return itemList.get(0);
        }
        return null;
    }

    private void processOrderBillingAndInventory(String orderSn, JsonNode detail, JsonNode firstItem, JsonNode orderIncome) {
        int quantity = firstItem.path("model_quantity_purchased")
                .asInt(firstItem.path("quantity_purchased").asInt(1));

        BigDecimal totalAmount = BigDecimal.valueOf(detail.path("total_amount").asDouble(0));

        ShopeeFinancials financials = calculateFinancials(orderSn, orderIncome, totalAmount);

        String sku = firstItem.path("model_sku").asText(firstItem.path("item_id").asText(orderSn));
        Product product = findProductBySku(sku);

        updateStockAndInventory(orderSn, sku, product, quantity);
        saveSale(orderSn, detail, firstItem, quantity, totalAmount, financials, product);
    }

    private ShopeeFinancials calculateFinancials(String orderSn, JsonNode orderIncome, BigDecimal totalAmount) {
        ShopeeFinancials financials = new ShopeeFinancials();
        financials.grossAmount = totalAmount;
        financials.marketplaceFee = BigDecimal.ZERO;
        financials.shippingCost = BigDecimal.ZERO;
        financials.netAmount = totalAmount;

        if (orderIncome != null && !orderIncome.isMissingNode()) {
            financials.grossAmount = BigDecimal.valueOf(orderIncome.path("original_price").asDouble(totalAmount.doubleValue()));
            financials.marketplaceFee = BigDecimal.valueOf(orderIncome.path("commission_fee").asDouble(0))
                    .add(BigDecimal.valueOf(orderIncome.path("service_fee").asDouble(0)));
            financials.shippingCost = BigDecimal.valueOf(orderIncome.path("actual_shipping_fee").asDouble(0));
            financials.netAmount = BigDecimal.valueOf(orderIncome.path("escrow_amount").asDouble(totalAmount.doubleValue()));

            logger.info("===== SHOPEE =====");
            logger.info("Order: {}", orderSn);
            logger.info("Gross: {}", financials.grossAmount);
            logger.info("Fee: {}", financials.marketplaceFee);
            logger.info("Shipping: {}", financials.shippingCost);
            logger.info("Net: {}", financials.netAmount);
            logger.info("==================");
        }
        return financials;
    }

    private Product findProductBySku(String sku) {
        if (sku != null && !sku.trim().isEmpty()) {
            return productRepository.findBySku(sku).orElse(null);
        }
        return null;
    }

    private void updateStockAndInventory(String orderSn, String sku, Product product, int quantity) {
        if (product == null) {
            return;
        }

        if (product.getCostPrice() != null) {
            logger.info("Produto encontrado: {}", product.getName());
            logger.info("SKU: {}", sku);
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        productRepository.save(product);

        StockMovement movement = new StockMovement();
        movement.setSku(sku);
        movement.setType(StockMovementType.SALE);
        movement.setQuantity(quantity);
        movement.setReference(orderSn);
        stockMovementRepository.save(movement);

        logger.info("Estoque atualizado: {}", product.getStockQuantity());
    }

    private void saveSale(String orderSn, JsonNode detail, JsonNode firstItem, int quantity,
                          BigDecimal totalAmount, ShopeeFinancials financials, Product product) {
        Sale sale = new Sale();
        sale.setOrderId(orderSn);
        sale.setMarketplace(Marketplace.SHOPEE);
        sale.setProductName(firstItem.path("item_name").asText("Pedido Shopee"));
        sale.setSku(firstItem.path("model_sku").asText(firstItem.path("item_id").asText(orderSn)));
        sale.setMarketplaceItemId(firstItem.path("item_id").asText());

        if (product != null && product.getCostPrice() != null) {
            sale.setProductCost(product.getCostPrice());
        } else {
            sale.setProductCost(BigDecimal.ZERO);
        }

        sale.setQuantity(quantity);
        sale.setSoldAt(getSoldAt(detail));
        sale.setGrossAmount(financials.grossAmount);
        sale.setNetAmount(financials.netAmount);

        sale.setUnitSalePrice(
                quantity > 0
                        ? totalAmount.divide(BigDecimal.valueOf(quantity), 2, java.math.RoundingMode.HALF_UP)
                        : totalAmount
        );

        sale.setMarketplaceFee(financials.marketplaceFee);
        sale.setShippingCost(financials.shippingCost);
        sale.setExtraCosts(BigDecimal.ZERO);

        sale.calculateProfit();
        saleRepository.save(sale);
    }

    private LocalDateTime getSoldAt(JsonNode detail) {
        long timestamp = detail.path("pay_time").asLong(detail.path("create_time").asLong(0));

        if (timestamp <= 0) {
            return LocalDateTime.now();
        }

        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(timestamp),
                ZoneId.systemDefault()
        );
    }

    private static class ShopeeFinancials {
        BigDecimal grossAmount;
        BigDecimal marketplaceFee;
        BigDecimal shippingCost;
        BigDecimal netAmount;
    }
}