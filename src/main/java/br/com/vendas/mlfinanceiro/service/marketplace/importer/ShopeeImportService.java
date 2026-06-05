package br.com.vendas.mlfinanceiro.service.marketplace.importer;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.integration.shopee.ShopeeClient;
import br.com.vendas.mlfinanceiro.repository.SaleRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class ShopeeImportService {

    private final ShopeeClient shopeeClient;

    private final SaleRepository saleRepository;

    public ShopeeImportService(
            ShopeeClient shopeeClient,
            SaleRepository saleRepository
    ) {

        this.shopeeClient =
                shopeeClient;

        this.saleRepository =
                saleRepository;
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

        while (hasMore
                && pagesRead < 5) {

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

        sale.setQuantity(
                quantity
        );

        sale.setSoldAt(
                getSoldAt(detail)
        );

        sale.setGrossAmount(
                totalAmount
        );

        sale.setNetAmount(
                totalAmount
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
                BigDecimal.ZERO
        );

        sale.setShippingCost(
                BigDecimal.ZERO
        );

        sale.setExtraCosts(
                BigDecimal.ZERO
        );

        sale.setProductCost(
                BigDecimal.ZERO
        );

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
