package br.com.vendas.mlfinanceiro.service.importer;

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

            mercadoLivreAuthService
                    .getValidAccessToken();

            mercadoLivreImported =
                    mercadoLivreImportService
                            .importOrders();

        } catch (Exception ignored) {
        }

        try {

            tokenService.getByMarketplace(
                    "SHOPEE"
            );

            shopeeImported =
                    shopeeImportService
                            .importOrders();

        } catch (Exception ignored) {
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

        return response;
    }
}
