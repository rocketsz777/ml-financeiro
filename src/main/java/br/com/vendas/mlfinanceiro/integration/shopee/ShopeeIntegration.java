package br.com.vendas.mlfinanceiro.integration.shopee;

import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.marketplace.MarketplaceIntegration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ShopeeIntegration
        implements MarketplaceIntegration {

    private final ShopeeClient shopeeClient;

    public ShopeeIntegration(
            ShopeeClient shopeeClient
    ) {

        this.shopeeClient =
                shopeeClient;
    }

    @Override
    public List<Sale> importSales() {

        System.out.println(
                "Importando vendas Shopee..."
        );

        /*
         FUTURAMENTE:
         - autenticar Shopee
         - buscar pedidos
         - converter JSON → Sale
         - salvar vendas
        */

        return new ArrayList<Sale>();
    }
}