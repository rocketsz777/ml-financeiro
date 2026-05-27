package br.com.vendas.mlfinanceiro.repository;

import br.com.vendas.mlfinanceiro.domain.Sale;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRepository
        extends JpaRepository<Sale, Long> {
}