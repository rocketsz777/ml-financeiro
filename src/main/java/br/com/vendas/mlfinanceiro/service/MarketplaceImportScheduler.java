package br.com.vendas.mlfinanceiro.service;

import br.com.vendas.mlfinanceiro.domain.Sale;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MarketplaceImportScheduler {

    private final MarketplaceService marketplaceService;

    public MarketplaceImportScheduler(
            MarketplaceService marketplaceService
    ) {

        this.marketplaceService =
                marketplaceService;
    }

    @Scheduled(fixedRate = 300000)
    public void importSalesAutomatically() {

        System.out.println(
                "Iniciando importação automática..."
        );

        List<Sale> sales =
                marketplaceService.importAllSales();

        System.out.println(
                "Vendas importadas: "
                        + sales.size()
        );
    }
}