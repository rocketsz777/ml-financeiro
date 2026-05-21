package br.com.vendas.mlfinanceiro.integration.mercadolivre;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.marketplace.MarketplaceIntegration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MercadoLivreIntegration
        implements MarketplaceIntegration {

    private final MercadoLivreClient mercadoLivreClient;

    public MercadoLivreIntegration(
            MercadoLivreClient mercadoLivreClient
    ) {

        this.mercadoLivreClient =
                mercadoLivreClient;
    }

    @Override
    public List<Sale> importSales() {

        System.out.println(
                "Importando vendas Mercado Livre..."
        );

        /*
         FUTURAMENTE:
         - buscar pedidos API ML
         - converter JSON → Sale
         - salvar vendas
        */

        return new ArrayList<Sale>();
    }
}