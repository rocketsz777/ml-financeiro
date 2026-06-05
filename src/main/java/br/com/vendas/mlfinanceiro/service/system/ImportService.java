package br.com.vendas.mlfinanceiro.service.system;

import br.com.vendas.mlfinanceiro.dto.ImportSalesResponse;
import br.com.vendas.mlfinanceiro.service.marketplace.auth.MercadoLivreAuthService;
import br.com.vendas.mlfinanceiro.service.marketplace.importer.MercadoLivreImportService;
import br.com.vendas.mlfinanceiro.service.marketplace.importer.ShopeeImportService;
import br.com.vendas.mlfinanceiro.service.marketplace.token.MarketplaceTokenService;
import org.springframework.stereotype.Service;

@Service
public class ImportService {

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

        this.mercadoLivreImportService =
                mercadoLivreImportService;

        this.mercadoLivreAuthService =
                mercadoLivreAuthService;

        this.shopeeImportService =
                shopeeImportService;

        this.tokenService =
                tokenService;
    }

    public ImportSalesResponse importSales() {

        ImportSalesResponse response =
                new ImportSalesResponse();

        int mercadoLivreImported =
                0;

        int shopeeImported =
                0;

        try {

            System.out.println(
                    "INICIANDO IMPORTAÇÃO ML"
            );

            mercadoLivreAuthService
                    .getValidAccessToken();

            mercadoLivreImported =
                    mercadoLivreImportService
                            .importOrders();

            System.out.println(
                    "IMPORTADOS ML: "
                            + mercadoLivreImported
            );

        } catch (Exception e) {

            System.out.println(
                    "ERRO MERCADO LIVRE:"
            );

            System.out.println(
                    e.getMessage()
            );

            e.printStackTrace();
        }

        try {

            System.out.println(
                    "INICIANDO IMPORTAÇÃO SHOPEE"
            );

            tokenService.getByMarketplace(
                    "SHOPEE"
            );

            shopeeImported =
                    shopeeImportService
                            .importOrders();

            System.out.println(
                    "IMPORTADOS SHOPEE: "
                            + shopeeImported
            );

        } catch (Exception e) {

            System.out.println(
                    "ERRO SHOPEE:"
            );

            System.out.println(
                    e.getMessage()
            );

            e.printStackTrace();
        }

        response.setMercadoLivreImported(
                mercadoLivreImported
        );

        response.setShopeeImported(
                shopeeImported
        );

        response.setTotalImported(
                mercadoLivreImported
                        + shopeeImported
        );

        System.out.println(
                "TOTAL IMPORTADO: "
                        + response.getTotalImported()
        );

        return response;
    }
}