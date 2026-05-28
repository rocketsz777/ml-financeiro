package br.com.vendas.mlfinanceiro.service;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.dto.SaleRequest;
import br.com.vendas.mlfinanceiro.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalesService {

    private final FileStoreService fileStoreService;

    private final SaleRepository saleRepository;

    public SalesService(

            FileStoreService fileStoreService,

            SaleRepository saleRepository
    ) {

        this.fileStoreService =
                fileStoreService;

        this.saleRepository =
                saleRepository;
    }

    // =========================================
    // PRODUTOS
    // =========================================

    public List<Product> listProducts() {

        return fileStoreService.loadProducts();
    }

    public Product createOrUpdateProduct(
            Product product
    ) {

        List<Product> products =
                fileStoreService.loadProducts();

        Optional<Product> existing =
                products.stream()

                        .filter(
                                p -> p.getSku()
                                        .equalsIgnoreCase(
                                                product.getSku()
                                        )
                        )

                        .findFirst();

        if (existing.isPresent()) {

            Product current =
                    existing.get();

            current.setName(
                    product.getName()
            );

            current.setMlItemId(
                    product.getMlItemId()
            );

            current.setCostPrice(
                    product.getCostPrice()
            );

            current.setStock(
                    product.getStock()
            );

        } else {

            products.add(
                    product
            );
        }

        fileStoreService.saveProducts(
                products
        );

        return product;
    }

    public void deleteProduct(
            String sku
    ) {

        List<Product> products =
                fileStoreService.loadProducts();

        products.removeIf(
                p -> p.getSku()
                        .equalsIgnoreCase(
                                sku
                        )
        );

        fileStoreService.saveProducts(
                products
        );
    }

    // =========================================
    // VENDAS
    // =========================================

    public Sale registerSale(
            SaleRequest request
    ) {

        List<Product> products =
                fileStoreService.loadProducts();

        Product product =
                products.stream()

                        .filter(
                                p -> p.getSku()
                                        .equalsIgnoreCase(
                                                request.getSku()
                                        )
                        )

                        .findFirst()

                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Produto não encontrado"
                                )
                        );

        if (
                product.getStock() <
                        request.getQuantity()
        ) {

            throw new RuntimeException(
                    "Estoque insuficiente"
            );
        }

        BigDecimal grossAmount =
                request.getUnitSalePrice()

                        .multiply(
                                BigDecimal.valueOf(
                                        request.getQuantity()
                                )
                        );

        BigDecimal marketplaceFee =
                Optional.ofNullable(
                        request.getMarketplaceFee()
                ).orElse(
                        BigDecimal.ZERO
                );

        BigDecimal shippingCost =
                Optional.ofNullable(
                        request.getShippingCost()
                ).orElse(
                        BigDecimal.ZERO
                );

        BigDecimal extraCosts =
                BigDecimal.ZERO;

        BigDecimal netAmount =
                grossAmount

                        .subtract(
                                marketplaceFee
                        )

                        .subtract(
                                shippingCost
                        );

        product.setStock(

                product.getStock()
                        - request.getQuantity()
        );

        fileStoreService.saveProducts(
                products
        );

        Sale sale =
                new Sale();

        sale.setOrderId(
                request.getOrderId()
        );

        sale.setSku(
                request.getSku()
        );

        sale.setProductName(

                request.getProductName() == null ||

                        request.getProductName()
                                .trim()
                                .isEmpty()

                        ?

                        product.getName()

                        :

                        request.getProductName()
        );

        sale.setMarketplace(
                Marketplace.MERCADO_LIVRE
        );

        sale.setQuantity(
                request.getQuantity()
        );

        sale.setGrossAmount(
                grossAmount
        );

        sale.setNetAmount(
                netAmount
        );

        sale.setUnitSalePrice(
                request.getUnitSalePrice()
        );

        sale.setProductCost(
                product.getCostPrice()
        );

        sale.setExtraCosts(
                extraCosts
        );

        sale.setMarketplaceFee(
                marketplaceFee
        );

        sale.setShippingCost(
                shippingCost
        );

        sale.calculateProfit();

        sale.setSoldAt(
                LocalDateTime.now()
        );

        saleRepository.save(
                sale
        );

        return sale;
    }

    public List<Sale> listSales() {

        return saleRepository.findAll();
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

        SaleRequest req =
                new SaleRequest();

        req.setOrderId(
                orderId
        );

        req.setSku(
                sku
        );

        req.setProductName(
                productName
        );

        req.setQuantity(
                quantity
        );

        req.setUnitSalePrice(
                unitPrice
        );

        req.setMarketplaceFee(
                marketplaceFee
        );

        req.setShippingCost(
                shippingCost
        );

        registerSale(
                req
        );
    }

    public List<Sale> findAll() {

        return saleRepository.findAll();
    }
}