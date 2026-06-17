package br.com.vendas.mlfinanceiro.repository;

import br.com.vendas.mlfinanceiro.domain.Sale;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository
        extends JpaRepository<Sale, Long> {

    boolean existsByOrderId(
            String orderId
    );

    @Query(
            "SELECT s.sku, " +
                    "SUM(s.quantity), " +
                    "SUM(s.netAmount), " +
                    "SUM(s.profit) " +
                    "FROM Sale s " +
                    "WHERE s.soldAt >= :startDate " +
                    "AND s.sku IS NOT NULL " +
                    "GROUP BY s.sku"
    )
    List<Object[]> getProductPerformance(
            @Param("startDate")
            LocalDateTime startDate
    );
}