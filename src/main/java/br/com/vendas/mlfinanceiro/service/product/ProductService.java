package br.com.vendas.mlfinanceiro.service.product;

import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(
            ProductRepository productRepository
    ) {
        this.productRepository =
                productRepository;
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
}