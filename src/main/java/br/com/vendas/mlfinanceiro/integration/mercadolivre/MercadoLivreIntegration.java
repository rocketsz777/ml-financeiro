package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.marketplace.MarketplaceIntegration;
import br.com.vendas.mlfinanceiro.service.marketplace.importer.MercadoLivreImportService;

import org.springframework.stereotype.Component;

import java.util.List;

// CORREÇÃO: adicionado @Component para registro no Spring
// e delegação real para MercadoLivreImportService
@Component
public class MercadoLivreIntegration
        implements MarketplaceIntegration {

    private final MercadoLivreImportService importService;

    public MercadoLivreIntegration(
            MercadoLivreImportService importService
    ) {
        this.importService = importService;
    }

    @Override
    public Marketplace getMarketplace() {
        return Marketplace.MERCADO_LIVRE;
    }

    @Override
    public List<Sale> importSales() {

        System.out.println(
                "Importando vendas Mercado Livre..."
        );

        // CORREÇÃO: antes retornava lista vazia
        // agora delega para o serviço real de importação
        return importService.importAndReturnSales();
    }
}