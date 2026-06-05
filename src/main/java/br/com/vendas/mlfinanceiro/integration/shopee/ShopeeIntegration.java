package br.com.vendas.mlfinanceiro.integration.shopee;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.marketplace.MarketplaceIntegration;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// CORREÇÃO: adicionado @Component para registro no Spring
@Component
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

        // TODO: implementar importação real da Shopee
        // igual ao MercadoLivreIntegration quando estiver pronto
        return new ArrayList<Sale>();
    }
}