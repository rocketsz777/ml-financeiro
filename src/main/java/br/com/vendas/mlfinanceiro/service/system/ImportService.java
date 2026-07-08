package br.com.vendas.mlfinanceiro.service.system;

import br.com.vendas.mlfinanceiro.dto.ImportSalesResponse;
import br.com.vendas.mlfinanceiro.service.marketplace.auth.MercadoLivreAuthService;
import br.com.vendas.mlfinanceiro.service.marketplace.importer.MercadoLivreImportService;
import br.com.vendas.mlfinanceiro.service.marketplace.importer.ShopeeImportService;
import br.com.vendas.mlfinanceiro.service.marketplace.token.MarketplaceTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ImportService {

    // Instanciando o Logger oficial do SLF4J para produção
    private static final Logger log = LoggerFactory.getLogger(ImportService.class);

    private final MercadoLivreImportService mercadoLivreImportService;
    private final MercadoLivreAuthService mercadoLivreAuthService;
    private final ShopeeImportService shopeeImportService;
    private final MarketplaceTokenService tokenService;

    public ImportService(
            MercadoLivreImportService mercadoLivreImportService,
            MercadoLivreAuthService mercadoLivreAuthService,
            ShopeeImportService shopeeImportService,
            MarketplaceTokenService tokenService
    ) {
        this.mercadoLivreImportService = mercadoLivreImportService;
        this.mercadoLivreAuthService = mercadoLivreAuthService;
        this.shopeeImportService = shopeeImportService;
        this.tokenService = tokenService;
    }

    public ImportSalesResponse importSales() {
        ImportSalesResponse response = new ImportSalesResponse();
        int mercadoLivreImported = 0;
        int shopeeImported = 0;

        // --- BLOCO MERCADO LIVRE ---
        try {
            log.info("Iniciando importação de vendas do Mercado Livre.");

            mercadoLivreAuthService.getValidAccessToken();
            mercadoLivreImported = mercadoLivreImportService.importOrders();

            log.info("Importação concluída com sucesso. Total Mercado Livre: {}", mercadoLivreImported);
        } catch (Exception e) {
            // log.error grava a mensagem e a stack trace de forma eficiente sem travar a aplicação
            log.error("Erro ao importar dados do Mercado Livre: {}", e.getMessage(), e);
        }

        // --- BLOCO SHOPEE ---
        try {
            log.info("Iniciando importação de vendas da Shopee.");

            tokenService.getByMarketplace("SHOPEE");
            shopeeImported = shopeeImportService.importOrders();

            log.info("Importação concluída com sucesso. Total Shopee: {}", shopeeImported);
        } catch (Exception e) {
            log.error("Erro ao importar dados da Shopee: {}", e.getMessage(), e);
        }

        // --- PROCESSAMENTO DO CORPO DA RESPOSTA ---
        int totalImported = mercadoLivreImported + shopeeImported;

        response.setMercadoLivreImported(mercadoLivreImported);
        response.setShopeeImported(shopeeImported);
        response.setTotalImported(totalImported);

        log.info("Resumo da execução - Total importado combinado: {}", totalImported);

        return response;
    }
}