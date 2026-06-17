package br.com.vendas.mlfinanceiro.service.product;

import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.repository.ProductRepository;
import br.com.vendas.mlfinanceiro.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import br.com.vendas.mlfinanceiro.dto.ProductPerformanceResponse;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final SaleRepository saleRepository;

    public ProductService(
            ProductRepository productRepository,
            SaleRepository saleRepository
    ) {
        this.productRepository =
                productRepository;

        this.saleRepository =
                saleRepository;
    }

    public List<Product> findAll() {

        return productRepository.findAll();
    }

    public Product findBySku(
            String sku
    ) {

        return productRepository
                .findBySku(sku)
                .orElseThrow(
                        () -> new RuntimeException(
                                "Produto não encontrado"
                        )
                );
    }

    public Product save(
            Product product
    ) {

        return productRepository.save(
                product
        );
    }

    public void delete(
            String sku
    ) {

        productRepository.deleteBySku(
                sku
        );
    }

    public Product addStock(
            String sku,
            Integer quantity
    ) {

        Product product =
                findBySku(sku);

        product.setStockQuantity(
                product.getStockQuantity()
                        + quantity
        );

        return productRepository.save(
                product
        );
    }

    public Product removeStock(
            String sku,
            Integer quantity
    ) {

        Product product =
                findBySku(sku);

        if (
                product.getStockQuantity()
                        < quantity
        ) {

            throw new RuntimeException(
                    "Estoque insuficiente"
            );
        }

        product.setStockQuantity(
                product.getStockQuantity()
                        - quantity
        );

        return productRepository.save(
                product
        );
    }
    public Map<String, ProductPerformanceResponse>
    getPerformance(
            String period
    ) {

        LocalDateTime startDate;

        switch (period) {

            case "WEEK":

                startDate =
                        LocalDate.now()
                                .with(
                                        DayOfWeek.MONDAY
                                )
                                .atStartOfDay();

                break;

            case "PREVIOUS_WEEK":

                startDate =
                        LocalDate.now()
                                .minusWeeks(1)
                                .with(
                                        DayOfWeek.MONDAY
                                )
                                .atStartOfDay();

                break;

            default:

                startDate =
                        LocalDate.now()
                                .withDayOfMonth(1)
                                .atStartOfDay();
        }

        List<Object[]> rows =
                saleRepository.getProductPerformance(
                        startDate
                );

        Map<String, ProductPerformanceResponse>
                result = new HashMap<>();

        for (Object[] row : rows) {

            String sku =
                    (String) row[0];

            if (
                    sku == null
                            || sku.trim().isEmpty()
            ) {

                continue;
            }

            result.put(

                    sku,

                    new ProductPerformanceResponse(

                            sku,

                            ((Long) row[1]).intValue(),

                            (BigDecimal) row[2],

                            (BigDecimal) row[3]
                    )
            );
        }

        return result;
    }
}