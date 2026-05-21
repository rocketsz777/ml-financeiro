package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.marketplace.MarketplaceIntegration;

import java.util.ArrayList;
import java.util.List;

public class MercadoLivreIntegration
        implements MarketplaceIntegration {

    @Override
    public Marketplace getMarketplace() {

        return Marketplace.MERCADO_LIVRE;
    }

    @Override
    public List<Sale> importSales() {

        System.out.println(
                "Importando vendas Mercado Livre..."
        );

        return new ArrayList<Sale>();
    }
}