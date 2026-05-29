package br.com.vendas.mlfinanceiro.repository;

import br.com.vendas.mlfinanceiro.domain.MarketplaceToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MarketplaceTokenRepository
        extends JpaRepository<MarketplaceToken, Long> {

    Optional<MarketplaceToken> findByMarketplace(
            String marketplace
    );
}