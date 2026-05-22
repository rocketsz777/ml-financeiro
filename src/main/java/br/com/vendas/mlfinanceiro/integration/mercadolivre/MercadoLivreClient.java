package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MercadoLivreClient {

    private final MercadoLivreTokenStore tokenStore;

    private final WebClient webClient;

    public MercadoLivreClient(
            MercadoLivreTokenStore tokenStore
    ) {

        this.tokenStore = tokenStore;

        this.webClient = WebClient.builder()
                .baseUrl(
                        "https://api.mercadolibre.com"
                )
                .build();
    }

    public String getMyUserData() {

        if (!tokenStore.hasToken()) {

            throw new RuntimeException(
                    "Mercado Livre não autenticado"
            );
        }

        String accessToken =
                tokenStore.getToken()
                        .getAccess_token();

        return webClient.get()
                .uri("/users/me")
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public MercadoLivreOrderResponse getOrders() {

        if (!tokenStore.hasToken()) {

            throw new RuntimeException(
                    "Mercado Livre não autenticado"
            );
        }

        String accessToken =
                tokenStore.getToken()
                        .getAccess_token();

        Long userId =
                tokenStore.getToken()
                        .getUser_id();

        return webClient.get()
                .uri(
                        "/orders/search?seller=" + userId
                )
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken
                )
                .retrieve()
                .bodyToMono(
                        MercadoLivreOrderResponse.class
                )
                .block();
    }
}