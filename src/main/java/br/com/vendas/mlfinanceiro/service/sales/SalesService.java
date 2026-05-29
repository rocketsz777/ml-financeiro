package br.com.vendas.mlfinanceiro.service.sales;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.dto.SaleRequest;
import br.com.vendas.mlfinanceiro.repository.ProductRepository;
import br.com.vendas.mlfinanceiro.repository.SaleRepository;
import br.com.vendas.mlfinanceiro.service.file.FileStoreService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class SalesService {

    private final FileStoreService fileStoreService;

    private final SaleRepository saleRepository;

    private final ProductRepository productRepository;

    public SalesService(

            FileStoreService fileStoreService,

            SaleRepository saleRepository,

            ProductRepository productRepository
    ) {

        this.fileStoreService =
                fileStoreService;

        this.saleRepository =
                saleRepository;

        this.productRepository =
                productRepository;
    }

    // =========================================
    // VENDAS
    // =========================================

    public Sale registerSale(
            SaleRequest request
    ) {

        Product product =
                productRepository

                        .findBySku(
                                request.getSku()
                        )

                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Produto não encontrado"
                                )
                        );

        if (
                product.getStockQuantity() <
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

        product.setStockQuantity(

                product.getStockQuantity()
                        - request.getQuantity()
        );

        productRepository.save(
                product
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