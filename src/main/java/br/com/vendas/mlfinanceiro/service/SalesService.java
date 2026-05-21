package br.com.vendas.mlfinanceiro.service;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.dto.SaleRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalesService {

    private final FileStoreService fileStoreService;

    public SalesService(FileStoreService fileStoreService) {
        this.fileStoreService = fileStoreService;
    }

    // =========================================
    // PRODUTOS
    // =========================================

    public List<Product> listProducts() {

        return fileStoreService.loadProducts();
    }

    public Product createOrUpdateProduct(Product product) {

        List<Product> products =
                fileStoreService.loadProducts();

        Optional<Product> existing =
                products.stream()
                        .filter(p ->
                                p.getSku()
                                        .equalsIgnoreCase(
                                                product.getSku()
                                        )
                        )
                        .findFirst();

        if (existing.isPresent()) {

            Product current = existing.get();

            current.setName(product.getName());
            current.setMlItemId(product.getMlItemId());
            current.setCostPrice(product.getCostPrice());
            current.setStock(product.getStock());

        } else {

            products.add(product);
        }

        fileStoreService.saveProducts(products);

        return product;
    }

    public void deleteProduct(String sku) {

        List<Product> products =
                fileStoreService.loadProducts();

        products.removeIf(p ->
                p.getSku()
                        .equalsIgnoreCase(sku)
        );

        fileStoreService.saveProducts(products);
    }

    // =========================================
    // VENDAS
    // =========================================

    public Sale registerSale(SaleRequest request) {

        List<Product> products =
                fileStoreService.loadProducts();

        Product product = products.stream()
                .filter(p ->
                        p.getSku().equalsIgnoreCase(
                                request.getSku()
                        )
                )
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "Produto não encontrado"
                        )
                );

        if (product.getStock() < request.getQuantity()) {

            throw new RuntimeException(
                    "Estoque insuficiente"
            );
        }

        BigDecimal productCost =
                product.getCostPrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        request.getQuantity()
                                )
                        );

        BigDecimal profit =
                request.getUnitSalePrice()
                        .multiply(
                                BigDecimal.valueOf(
                                        request.getQuantity()
                                )
                        )
                        .subtract(productCost)
                        .subtract(
                                Optional.ofNullable(
                                        request.getMarketplaceFee()
                                ).orElse(BigDecimal.ZERO)
                        )
                        .subtract(
                                Optional.ofNullable(
                                        request.getShippingCost()
                                ).orElse(BigDecimal.ZERO)
                        );

        product.setStock(
                product.getStock() -
                        request.getQuantity()
        );

        fileStoreService.saveProducts(products);

        List<Sale> sales =
                fileStoreService.loadSales();

        Sale sale = new Sale(
                request.getOrderId(),

                request.getSku(),

                request.getProductName() == null ||
                        request.getProductName()
                                .trim()
                                .isEmpty()
                        ? product.getName()
                        : request.getProductName(),

                Marketplace.MERCADO_LIVRE,

                request.getQuantity(),

                request.getUnitSalePrice(),

                productCost,

                Optional.ofNullable(
                        request.getMarketplaceFee()
                ).orElse(BigDecimal.ZERO),

                Optional.ofNullable(
                        request.getShippingCost()
                ).orElse(BigDecimal.ZERO),

                profit,

                LocalDateTime.now()
        );

        sales.add(sale);

        fileStoreService.saveSales(sales);

        return sale;
    }

    public List<Sale> listSales() {

        return fileStoreService.loadSales();
    }

    public void ingestMLOrder(
            String orderId,
            String sku,
            String productName,
            int quantity,
            BigDecimal unitPrice,
            BigDecimal marketplaceFee,
            BigDecimal shippingCost
    ) {

        SaleRequest req = new SaleRequest();

        req.setOrderId(orderId);
        req.setSku(sku);
        req.setProductName(productName);
        req.setQuantity(quantity);
        req.setUnitSalePrice(unitPrice);
        req.setMarketplaceFee(marketplaceFee);
        req.setShippingCost(shippingCost);

        registerSale(req);
    }
}