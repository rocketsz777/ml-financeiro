package br.com.vendas.mlfinanceiro.repository;

import br.com.vendas.mlfinanceiro.domain.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository
        extends JpaRepository<Product, Long> {

    Optional<Product> findBySku(
            String sku
    );

    void deleteBySku(
            String sku
    );

    boolean existsBySku(
            String sku
    );

    Optional<Product> findByMlItemId(
            String mlItemId
    );
}