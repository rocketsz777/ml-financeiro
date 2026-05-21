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
            new HashMap<
                    Marketplace,
                    MarketplaceIntegration
                    >();

    public MarketplaceFactory() {

        register(
                new MercadoLivreIntegration()
        );

        register(
                new ShopeeIntegration()
        );
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
                integrations.get(
                        marketplace
                );

        if (integration == null) {

            throw new RuntimeException(
                    "Marketplace não suportado"
            );
        }

        return integration;
    }
}