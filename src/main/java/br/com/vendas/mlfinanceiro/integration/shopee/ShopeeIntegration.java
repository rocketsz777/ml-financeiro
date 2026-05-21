package br.com.vendas.mlfinanceiro.integration.shopee;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.marketplace.MarketplaceIntegration;

import java.util.ArrayList;
import java.util.List;

public class ShopeeIntegration
        implements MarketplaceIntegration {

    @Override
    public Marketplace getMarketplace() {

        return Marketplace.SHOPEE;
    }

    @Override
    public List<Sale> importSales() {

        System.out.println(
                "Importando vendas Shopee..."
        );

        return new ArrayList<Sale>();
    }
}