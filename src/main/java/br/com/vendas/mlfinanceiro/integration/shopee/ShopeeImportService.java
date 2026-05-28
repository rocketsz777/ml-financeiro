package br.com.vendas.mlfinanceiro.integration.shopee;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.domain.StockMovement;
import br.com.vendas.mlfinanceiro.domain.StockMovementType;
import br.com.vendas.mlfinanceiro.service.FileStoreService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ShopeeImportService {

    private final FileStoreService fileStoreService;

    public ShopeeImportService(
            FileStoreService fileStoreService
    ) {

        this.fileStoreService =
                fileStoreService;
    }

    public void simulateImport() {

        List<Sale> sales =
                fileStoreService.loadSales();

        List<Product> products =
                fileStoreService.loadProducts();

        List<StockMovement> movements =
                fileStoreService.loadStockMovements();

        Sale sale =
                new Sale();

        sale.setOrderId(
                "SHOPEE-001"
        );

        sale.setSku(
                "BOL001"
        );

        sale.setProductName(
                "Bolinha Anti Stress"
        );

        sale.setMarketplace(
                Marketplace.SHOPEE
        );

        sale.setQuantity(
                1
        );

        sale.setGrossAmount(
                new BigDecimal(
                        "49.90"
                )
        );

        sale.setNetAmount(
                new BigDecimal(
                        "42.00"
                )
        );

        sale.setUnitSalePrice(
                new BigDecimal(
                        "49.90"
                )
        );

        sale.setProductCost(
                new BigDecimal(
                        "20.50"
                )
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

        sale.calculateProfit();

        sale.setSoldAt(
                LocalDateTime.now()
        );

        sales.add(
                sale
        );

        Product matchedProduct =
                products.stream()
                        .filter(
                                p -> p.getSku()
                                        .equalsIgnoreCase(
                                                sale.getSku()
                                        )
                        )
                        .findFirst()
                        .orElse(
                                null
                        );

        if (matchedProduct != null) {

            matchedProduct.setStock(
                    matchedProduct.getStock()
                            - sale.getQuantity()
            );

            StockMovement movement =
                    new StockMovement();

            movement.setSku(
                    matchedProduct.getSku()
            );

            movement.setType(
                    StockMovementType.SALE
            );

            movement.setQuantity(
                    -sale.getQuantity()
            );

            movement.setReference(
                    sale.getOrderId()
            );

            movement.setCreatedAt(
                    LocalDateTime.now()
            );

            movements.add(
                    movement
            );

            fileStoreService.saveStockMovements(
                    movements
            );
        }

        fileStoreService.saveProducts(
                products
        );

        fileStoreService.saveSales(
                sales
        );
    }
}