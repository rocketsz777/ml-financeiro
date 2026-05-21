package br.com.vendas.mlfinanceiro.marketplace;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.MercadoLivreIntegration;
import br.com.vendas.mlfinanceiro.integration.shopee.ShopeeIntegration;
import org.springframework.stereotype.Component;

@Component
public class MarketplaceFactory {

    private final MercadoLivreIntegration mercadoLivreIntegration;

    private final ShopeeIntegration shopeeIntegration;

    public MarketplaceFactory(
            MercadoLivreIntegration mercadoLivreIntegration,
            ShopeeIntegration shopeeIntegration
    ) {

        this.mercadoLivreIntegration =
                mercadoLivreIntegration;

        this.shopeeIntegration =
                shopeeIntegration;
    }

    public MarketplaceIntegration getIntegration(
            Marketplace marketplace
    ) {

        switch (marketplace) {

            case MERCADO_LIVRE:
                return mercadoLivreIntegration;

            case SHOPEE:
                return shopeeIntegration;

            default:
                throw new IllegalArgumentException(
                        "Marketplace não suportado"
                );
        }
    }
}