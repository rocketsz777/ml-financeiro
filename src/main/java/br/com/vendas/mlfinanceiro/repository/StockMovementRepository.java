package br.com.vendas.mlfinanceiro.repository;

import br.com.vendas.mlfinanceiro.domain.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMovementRepository
        extends JpaRepository<StockMovement, Long> {
}