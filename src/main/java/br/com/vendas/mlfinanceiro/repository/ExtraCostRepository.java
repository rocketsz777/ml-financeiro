package br.com.vendas.mlfinanceiro.repository;

import br.com.vendas.mlfinanceiro.domain.ExtraCost;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExtraCostRepository
        extends JpaRepository<ExtraCost, Long> {

    List<ExtraCost> findByActiveTrue();
}