package br.com.vendas.mlfinanceiro.marketplace;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.MercadoLivreIntegration;
import br.com.vendas.mlfinanceiro.integration.shopee.ShopeeIntegration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class MarketplaceFactory {

    private final Map<
            Marketplace,
            MarketplaceIntegration
            > integrations =
            new HashMap<>();

    // CORREÇÃO: injetar via Spring em vez de usar new
    // para que as dependências de cada integração sejam resolvidas
    public MarketplaceFactory(
            MercadoLivreIntegration mercadoLivreIntegration,
            ShopeeIntegration shopeeIntegration
    ) {
        register(mercadoLivreIntegration);
        register(shopeeIntegration);
    }

    private void register(
            MarketplaceIntegration integration
    ) {
        integrations.put(
                integration.getMarketplace(),
                integration
        );
    }

    public MarketplaceIntegration getIntegration(
            Marketplace marketplace
    ) {
        MarketplaceIntegration integration =
                integrations.get(marketplace);

        if (integration == null) {
            throw new RuntimeException(
                    "Marketplace não suportado"
            );
        }

        return integration;
    }
}