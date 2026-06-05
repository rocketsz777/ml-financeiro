package br.com.vendas.mlfinanceiro.service.system;

import br.com.vendas.mlfinanceiro.dto.SystemStatusResponse;
import br.com.vendas.mlfinanceiro.service.marketplace.auth.MercadoLivreAuthService;
import br.com.vendas.mlfinanceiro.service.marketplace.token.MarketplaceTokenService;
import org.springframework.stereotype.Service;

@Service
public class SystemService {

    private final MercadoLivreAuthService mercadoLivreAuthService;

    private final MarketplaceTokenService tokenService;

    public SystemService(
            MercadoLivreAuthService mercadoLivreAuthService,
            MarketplaceTokenService tokenService
    ) {

        this.mercadoLivreAuthService =
                mercadoLivreAuthService;

        this.tokenService =
                tokenService;
    }

    public SystemStatusResponse getStatus() {

        SystemStatusResponse response =
                new SystemStatusResponse();

        response.setApplication(
                "ONLINE"
        );

        response.setDatabase(
                "ONLINE"
        );

        try {

            mercadoLivreAuthService
                    .getValidAccessToken();

            response.setMercadoLivre(
                    "CONECTADO"
            );

        } catch (Exception e) {

            response.setMercadoLivre(
                    "DESCONECTADO"
            );
        }

        try {

            tokenService.getByMarketplace(
                    "SHOPEE"
            );

            response.setShopee(
                    "CONECTADO"
            );

        } catch (Exception e) {

            response.setShopee(
                    "DESCONECTADO"
            );
        }

        return response;
    }
}