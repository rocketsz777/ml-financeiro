package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class MercadoLivreAuthService {

    @Value("${mercadolivre.client-id}")
    private String clientId;

    @Value("${mercadolivre.client-secret}")
    private String clientSecret;

    @Value("${mercadolivre.redirect-uri}")
    private String redirectUri;

    private final WebClient webClient;

    public MercadoLivreAuthService() {

        this.webClient = WebClient.builder()
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

        return webClient.post()
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
    }
}