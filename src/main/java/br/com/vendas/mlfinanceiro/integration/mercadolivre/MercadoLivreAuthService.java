package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;

@Service
public class MercadoLivreAuthService {

    @Value("${mercadolivre.client-id}")
    private String clientId;

    @Value("${mercadolivre.client-secret}")
    private String clientSecret;

    @Value("${mercadolivre.redirect-uri}")
    private String redirectUri;

    private final MercadoLivreTokenStore tokenStore;

    private final WebClient webClient;

    public MercadoLivreAuthService(
            MercadoLivreTokenStore tokenStore
    ) {

        this.tokenStore =
                tokenStore;

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
                + "&client_id=" + clientId
                + "&redirect_uri=" + redirectUri;
    }

    public MercadoLivreTokenResponse exchangeCodeForToken(
            String code
    ) {

        String requestBody =
                "grant_type=authorization_code"
                        + "&client_id=" + clientId
                        + "&client_secret=" + clientSecret
                        + "&code=" + code
                        + "&redirect_uri=" + redirectUri;

        MercadoLivreTokenResponse token =
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

        if (token != null) {

            token.setCreated_at(
                    LocalDateTime.now()
            );
        }

        return token;
    }

    public void refreshTokenIfNeeded() {

        if (!tokenStore.hasToken()) {

            return;
        }

        MercadoLivreTokenResponse token =
                tokenStore.getToken();

        if (token.getCreated_at() == null) {

            refreshAccessToken();

            return;
        }

        LocalDateTime expiresAt =
                token.getCreated_at()
                        .plusSeconds(
                                token.getExpires_in()
                        );

        if (LocalDateTime.now()
                .isAfter(
                        expiresAt.minusMinutes(5)
                )) {

            refreshAccessToken();
        }
    }

    public void refreshAccessToken() {

        MercadoLivreTokenResponse currentToken =
                tokenStore.getToken();

        if (currentToken == null) {

            return;
        }

        String requestBody =
                "grant_type=refresh_token"
                        + "&client_id=" + clientId
                        + "&client_secret=" + clientSecret
                        + "&refresh_token="
                        + currentToken.getRefresh_token();

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

        if (newToken != null) {

            newToken.setCreated_at(
                    LocalDateTime.now()
            );

            tokenStore.save(
                    newToken
            );
        }
    }
}