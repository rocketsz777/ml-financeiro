package br.com.vendas.mlfinanceiro.service.marketplace.importer;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.marketplace.MarketplaceFactory;
import br.com.vendas.mlfinanceiro.marketplace.MarketplaceIntegration;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MarketplaceService {

    private final MarketplaceFactory marketplaceFactory;

    public MarketplaceService(
            MarketplaceFactory marketplaceFactory
    ) {

        this.marketplaceFactory =
                marketplaceFactory;
    }

    public List<Sale> importSales(
            Marketplace marketplace
    ) {

        MarketplaceIntegration integration =
                marketplaceFactory.getIntegration(
                        marketplace
                );

        return integration.importSales();
    }

    public List<Sale> importAllSales() {

        List<Sale> allSales =
                new ArrayList<Sale>();

        for (Marketplace marketplace :
                Marketplace.values()) {

            try {

                allSales.addAll(
                        importSales(marketplace)
                );

            } catch (Exception e) {

                System.out.println(
                        "Erro ao importar vendas de: "
                                + marketplace
                );
            }
        }

        return allSales;
    }
}