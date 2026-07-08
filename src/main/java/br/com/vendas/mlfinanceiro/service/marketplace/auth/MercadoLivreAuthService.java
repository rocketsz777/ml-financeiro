package br.com.vendas.mlfinanceiro.service.marketplace.auth;

import br.com.vendas.mlfinanceiro.domain.MarketplaceToken;
import br.com.vendas.mlfinanceiro.exception.BusinessException;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.MercadoLivreTokenResponse;
import br.com.vendas.mlfinanceiro.service.marketplace.token.MarketplaceTokenService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MercadoLivreAuthService {

    // CONSTANTES
    private static final String CLIENT_ID_PARAM = "&client_id=";
    private static final String MARKETPLACE_NAME = "MERCADO_LIVRE";

    @Value("${mercadolivre.client-id}")
    private String clientId;

    @Value("${mercadolivre.client-secret}")
    private String clientSecret;

    @Value("${mercadolivre.redirect-uri}")
    private String redirectUri;

    private final MarketplaceTokenService tokenService;

    private final WebClient webClient;

    public MercadoLivreAuthService(
            MarketplaceTokenService tokenService
    ) {

        this.tokenService =
                tokenService;

        this.webClient =
                WebClient.builder()
                        .baseUrl(
                                "https://api.mercadolibre.com"
                        )
                        .build();
    }

    public String generateAuthorizationUrl() {

        return "https://auth.mercadolivre.com.br/authorization"
                + "?response_type=code"
                + CLIENT_ID_PARAM + clientId
                + "&redirect_uri=" + redirectUri;
    }

    public MercadoLivreTokenResponse exchangeCodeForToken(
            String code
    ) {

        String requestBody =
                "grant_type=authorization_code"
                        + CLIENT_ID_PARAM + clientId
                        + "&client_secret=" + clientSecret
                        + "&code=" + code
                        + "&redirect_uri=" + redirectUri;

        MercadoLivreTokenResponse newToken =
                webClient.post()
                        .uri("/oauth/token")
                        .contentType(
                                MediaType.APPLICATION_FORM_URLENCODED
                        )
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(
                                MercadoLivreTokenResponse.class
                        )
                        .block();

        System.out.println(
                "TOKEN RESPONSE: " + newToken
        );

        if (newToken != null) {

            System.out.println(
                    "SALVANDO TOKEN NO BANCO"
            );

            tokenService.save(
                    MARKETPLACE_NAME,
                    String.valueOf(
                            newToken.getUser_id()
                    ),
                    newToken.getAccess_token(),
                    newToken.getRefresh_token(),
                    newToken.getExpires_in().longValue()
            );
        }

        return newToken;
    }

    public String getValidAccessToken() {

        MarketplaceToken token =
                tokenService.getByMarketplace(
                        MARKETPLACE_NAME
                );

        if (tokenService.isExpired(token)) {

            refreshAccessToken();
        }

        return tokenService
                .getByMarketplace(
                        MARKETPLACE_NAME
                )
                .getAccessToken();
    }

    public void refreshAccessToken() {

        MarketplaceToken currentToken =
                tokenService.getByMarketplace(
                        MARKETPLACE_NAME
                );

        String requestBody =
                "grant_type=refresh_token"
                        + CLIENT_ID_PARAM + clientId
                        + "&client_secret=" + clientSecret
                        + "&refresh_token="
                        + currentToken.getRefreshToken();

        MercadoLivreTokenResponse newToken =
                webClient.post()
                        .uri("/oauth/token")
                        .contentType(
                                MediaType.APPLICATION_FORM_URLENCODED
                        )
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(
                                MercadoLivreTokenResponse.class
                        )
                        .block();

        if (newToken == null) {

            throw new BusinessException(
                    "Erro ao atualizar token Mercado Livre"
            );
        }

        tokenService.save(
                MARKETPLACE_NAME,
                String.valueOf(
                        newToken.getUser_id()
                ),
                newToken.getAccess_token(),
                newToken.getRefresh_token(),
                newToken.getExpires_in().longValue()
        );
    }
}