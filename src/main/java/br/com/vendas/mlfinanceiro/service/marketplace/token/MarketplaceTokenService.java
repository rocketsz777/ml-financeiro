package br.com.vendas.mlfinanceiro.service.marketplace.token;

import br.com.vendas.mlfinanceiro.domain.MarketplaceToken;
import br.com.vendas.mlfinanceiro.exception.ResourceNotFoundException;
import br.com.vendas.mlfinanceiro.repository.MarketplaceTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MarketplaceTokenService {

    private final MarketplaceTokenRepository repository;

    public MarketplaceToken save(
            String marketplace,
            String sellerId,
            String accessToken,
            String refreshToken,
            Long expiresInSeconds
    ) {

        MarketplaceToken token = repository
                .findByMarketplace(marketplace)
                .orElse(new MarketplaceToken());

        token.setMarketplace(marketplace);
        token.setSellerId(sellerId);

        token.setAccessToken(accessToken);
        token.setRefreshToken(refreshToken);

        token.setExpiresAt(
                LocalDateTime.now()
                        .plusSeconds(expiresInSeconds)
        );

        token.setUpdatedAt(LocalDateTime.now());

        return repository.save(token);
    }

    public MarketplaceToken getByMarketplace(
            String marketplace
    ) {

        return repository.findByMarketplace(marketplace)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Token não encontrado"
                        )
                );
    }

    public boolean isExpired(
            MarketplaceToken token
    ) {

        return token.getExpiresAt()
                .isBefore(
                        LocalDateTime.now()
                                .plusMinutes(5)
                );
    }
}