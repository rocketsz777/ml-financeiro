package br.com.vendas.mlfinanceiro.service.marketplace.importer;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.integration.shopee.ShopeeClient;
import br.com.vendas.mlfinanceiro.repository.ProductRepository;
import br.com.vendas.mlfinanceiro.repository.SaleRepository;
import com.fasterxml.jackson.databind.JsonNode;
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

        this.shopeeClient =
                shopeeClient;

        this.saleRepository =
                saleRepository;

        this.productRepository =
                productRepository;

        this.stockMovementRepository =
                stockMovementRepository;
    }

    public int importOrders() {

        int importedCount =
                0;

        String cursor =
                null;

        boolean hasMore =
                true;

        int pagesRead =
                0;

        while (hasMore && pagesRead < 100){

            JsonNode orderListResponse =
                    shopeeClient.getOrderList(
                            cursor
                    );

            JsonNode response =
                    orderListResponse == null
                            ? null
                            : orderListResponse.get(
                                    "response"
                            );

            JsonNode orders =
                    response == null
                            ? null
                            : response.get(
                                    "order_list"
                            );

            if (orders == null
                    || !orders.isArray()) {

                return importedCount;
            }

            for (JsonNode order : orders) {

                String orderSn =
                        order.path(
                                "order_sn"
                        ).asText();

                if (orderSn.trim().isEmpty()
                        || saleRepository.existsByOrderId(
                                orderSn
                        )) {

                    continue;
                }

                if (importOrderDetail(orderSn)) {

                    importedCount++;
                }
            }

            hasMore =
                    response != null
                            && response.path(
                                    "more"
                            ).asBoolean(false);

            cursor =
                    response == null
                            ? null
                            : response.path(
                                    "next_cursor"
                            ).asText(null);

            pagesRead++;
        }

        return importedCount;
    }

    private boolean importOrderDetail(
            String orderSn
    ) {

        JsonNode detailResponse =
                shopeeClient.getOrderDetail(
                        orderSn
                );

        JsonNode orders =
                detailResponse == null
                        ? null
                        : detailResponse.path(
                                "response"
                        ).path(
                                "order_list"
                        );

        if (orders == null
                || !orders.isArray()
                || orders.size() == 0) {

            return false;
        }

        JsonNode detail =
                orders.get(0);

        JsonNode escrowResponse =
                shopeeClient.getEscrowDetail(orderSn);

        JsonNode orderIncome =
                escrowResponse == null
                        ? null
                        : escrowResponse.path("response")
                        .path("order_income");

        JsonNode firstItem =
                detail.path(
                        "item_list"
                ).isArray()
                        && detail.path(
                                "item_list"
                        ).size() > 0
                        ? detail.path(
                                "item_list"
                        ).get(0)
                        : null;

        if (firstItem == null) {

            return false;
        }

        int quantity =
                firstItem.path(
                        "model_quantity_purchased"
                ).asInt(
                        firstItem.path(
                                "quantity_purchased"
                        ).asInt(1)
                );

        BigDecimal totalAmount =
                BigDecimal.valueOf(
                        detail.path(
                                "total_amount"
                        ).asDouble(0)
                );
        BigDecimal grossAmount = totalAmount;
        BigDecimal marketplaceFee = BigDecimal.ZERO;
        BigDecimal shippingCost = BigDecimal.ZERO;
        BigDecimal netAmount = totalAmount;

        if (orderIncome != null && !orderIncome.isMissingNode()) {

            grossAmount =
                    BigDecimal.valueOf(
                            orderIncome.path("original_price")
                                    .asDouble(totalAmount.doubleValue())
                    );

            marketplaceFee =
                    BigDecimal.valueOf(
                            orderIncome.path("commission_fee")
                                    .asDouble(0)
                    ).add(
                            BigDecimal.valueOf(
                                    orderIncome.path("service_fee")
                                            .asDouble(0)
                            )
                    );

            shippingCost =
                    BigDecimal.valueOf(
                            orderIncome.path("actual_shipping_fee")
                                    .asDouble(0)
                    );

            netAmount =
                    BigDecimal.valueOf(
                            orderIncome.path("escrow_amount")
                                    .asDouble(totalAmount.doubleValue())
                    );

            System.out.println("===== SHOPEE =====");
            System.out.println("Order: " + orderSn);
            System.out.println("Gross: " + grossAmount);
            System.out.println("Fee: " + marketplaceFee);
            System.out.println("Shipping: " + shippingCost);
            System.out.println("Net: " + netAmount);
            System.out.println("==================");
        }
        Sale sale =
                new Sale();

        sale.setOrderId(
                orderSn
        );

        sale.setMarketplace(
                Marketplace.SHOPEE
        );

        sale.setProductName(
                firstItem.path(
                        "item_name"
                ).asText("Pedido Shopee")
        );

        sale.setSku(
                firstItem.path(
                        "model_sku"
                ).asText(
                        firstItem.path(
                                "item_id"
                        ).asText(orderSn)
                )
        );
        sale.setMarketplaceItemId(
                firstItem.path("item_id").asText()
        );

        String sku =
                sale.getSku();

        Product product =
                null;

        if (sku != null
                && !sku.trim().isEmpty()) {

            product =
                    productRepository
                            .findBySku(
                                    sku
                            )
                            .orElse(null);
        }

        if (product != null
                && product.getCostPrice() != null) {

            sale.setProductCost(
                    product.getCostPrice()
            );

            System.out.println(
                    "Produto encontrado: "
                            + product.getName()
            );

            System.out.println(
                    "SKU: "
                            + sku
            );
        }
        if (product != null) {

            product.setStockQuantity(
                    product.getStockQuantity()
                            - quantity
            );

            productRepository.save(
                    product
            );

            StockMovement movement =
                    new StockMovement();

            movement.setSku(
                    sku
            );

            movement.setType(
                    StockMovementType.SALE
            );

            movement.setQuantity(
                    quantity
            );

            movement.setReference(
                    orderSn
            );

            stockMovementRepository.save(
                    movement
            );

            System.out.println(
                    "Estoque atualizado: "
                            + product.getStockQuantity()
            );
        }

        sale.setQuantity(
                quantity
        );

        sale.setSoldAt(
                getSoldAt(detail)
        );

        sale.setGrossAmount(
                grossAmount
        );

        sale.setNetAmount(
                netAmount
        );

        sale.setUnitSalePrice(
                quantity > 0
                        ? totalAmount.divide(
                                BigDecimal.valueOf(quantity),
                                2,
                                java.math.RoundingMode.HALF_UP
                        )
                        : totalAmount
        );

        sale.setMarketplaceFee(
                marketplaceFee
        );

        sale.setShippingCost(
                shippingCost
        );

        sale.setExtraCosts(
                BigDecimal.ZERO
        );

        if (sale.getProductCost() == null) {

            sale.setProductCost(
                    BigDecimal.ZERO
            );
        }

        sale.calculateProfit();

        saleRepository.save(
                sale
        );

        return true;
    }

    private LocalDateTime getSoldAt(
            JsonNode detail
    ) {

        long timestamp =
                detail.path(
                        "pay_time"
                ).asLong(
                        detail.path(
                                "create_time"
                        ).asLong(0)
                );

        if (timestamp <= 0) {

            return LocalDateTime.now();
        }

        return LocalDateTime.ofInstant(
                Instant.ofEpochSecond(
                        timestamp
                ),
                ZoneId.systemDefault()
        );
    }
}
