package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResult;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreOrderDetail;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreOrderItem;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivrePayment;
import br.com.vendas.mlfinanceiro.service.FileStoreService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Service
public class MercadoLivreImportService {

    private final MercadoLivreClient mercadoLivreClient;

    private final FileStoreService fileStoreService;

    public MercadoLivreImportService(
            MercadoLivreClient mercadoLivreClient,
            FileStoreService fileStoreService
    ) {

        this.mercadoLivreClient =
                mercadoLivreClient;

        this.fileStoreService =
                fileStoreService;
    }

    public int importOrders() {

        List<MercadoLivreOrderResult> orders =
                mercadoLivreClient
                        .getOrders()
                        .getResults();

        List<Sale> sales =
                fileStoreService.loadSales();

        List<Product> products =
                fileStoreService.loadProducts();

        int imported = 0;

        for (MercadoLivreOrderResult order : orders) {

            boolean alreadyExists =
                    sales.stream()
                            .anyMatch(
                                    s -> order.getId()
                                            .toString()
                                            .equals(
                                                    s.getOrderId()
                                            )
                            );

            if (alreadyExists) {

                continue;
            }

            MercadoLivreOrderDetail detail =
                    mercadoLivreClient
                            .getOrderDetail(
                                    order.getId()
                            );

            if (detail.getOrder_items() == null ||
                    detail.getOrder_items().isEmpty()) {

                continue;
            }

            MercadoLivreOrderItem firstItem =
                    detail.getOrder_items()
                            .get(0);

            MercadoLivrePayment payment =
                    detail.getPayments() != null
                            && !detail.getPayments().isEmpty()
                            ? detail.getPayments()
                            .get(0)
                            : null;

            Sale sale = new Sale();

            sale.setOrderId(
                    detail.getId().toString()
            );

            String sellerSku =
                    firstItem.getItem()
                            .getSeller_sku();

            if (sellerSku == null ||
                    sellerSku.trim().isEmpty()) {

                sellerSku = "NO-SKU";
            }

            sellerSku =
                    normalizeSku(
                            sellerSku
                    );

            final String lookupSku =
                    sellerSku;

            sale.setSku(
                    sellerSku
            );

            sale.setProductName(
                    firstItem.getItem()
                            .getTitle()
            );

            sale.setMarketplace(
                    Marketplace.MERCADO_LIVRE
            );

            sale.setQuantity(
                    firstItem.getQuantity()
            );

            BigDecimal quantity =
                    BigDecimal.valueOf(
                            firstItem.getQuantity()
                    );

            BigDecimal grossAmount =
                    detail.getTotal_amount();

            sale.setGrossAmount(
                    grossAmount
            );

            BigDecimal unitPrice =
                    grossAmount.divide(
                            quantity,
                            2,
                            BigDecimal.ROUND_HALF_UP
                    );

            sale.setUnitSalePrice(
                    unitPrice
            );

            Product matchedProduct =
                    products.stream()
                            .filter(
                                    p -> p.getSku() != null
                                            && normalizeSku(
                                            p.getSku()
                                    ).equalsIgnoreCase(
                                            lookupSku
                                    )
                            )
                            .findFirst()
                            .orElse(null);

            BigDecimal productCost =
                    BigDecimal.ZERO;

            if (matchedProduct != null &&
                    matchedProduct.getCostPrice() != null) {

                productCost =
                        matchedProduct.getCostPrice();
            }

            sale.setProductCost(
                    productCost
            );

            BigDecimal marketplaceFee =
                    payment != null
                            && payment.getMarketplace_fee() != null
                            ? payment.getMarketplace_fee()
                            : BigDecimal.ZERO;

            sale.setMarketplaceFee(
                    marketplaceFee
            );

            sale.setShippingCost(
                    BigDecimal.ZERO
            );

            sale.setExtraCosts(
                    BigDecimal.ZERO
            );

            BigDecimal netAmount =
                    grossAmount
                            .subtract(
                                    marketplaceFee
                            )
                            .subtract(
                                    sale.getShippingCost()
                            );

            sale.setNetAmount(
                    netAmount
            );

            BigDecimal totalProductCost =
                    productCost.multiply(
                            quantity
                    );

            sale.setProfit(
                    netAmount
                            .subtract(
                                    totalProductCost
                            )
                            .subtract(
                                    sale.getExtraCosts()
                            )
            );

            sale.setSoldAt(
                    OffsetDateTime.parse(
                            order.getDate_created()
                    ).toLocalDateTime()
            );

            sales.add(sale);

            imported++;
        }

        fileStoreService.saveSales(
                sales
        );

        return imported;
    }

    private String normalizeSku(
            String sku
    ) {

        if (sku == null) {

            return "";
        }

        return sku
                .trim()
                .replaceAll("[^a-zA-Z0-9]", "")
                .replaceFirst("^0+(?!$)", "")
                .toUpperCase();
    }
}