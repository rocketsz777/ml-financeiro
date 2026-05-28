package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResponse;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreOrderDetail;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MercadoLivreClient {

    private final MercadoLivreTokenStore tokenStore;

    private final MercadoLivreAuthService authService;

    private final WebClient webClient;

    public MercadoLivreClient(
            MercadoLivreTokenStore tokenStore,
            MercadoLivreAuthService authService
    ) {

        this.tokenStore =
                tokenStore;

        this.authService =
                authService;

        this.webClient =
                WebClient.builder()
                        .baseUrl(
                                "https://api.mercadolibre.com"
                        )
                        .build();
    }

    public String getMyUserData() {

        authService.refreshTokenIfNeeded();

        validateAuthentication();

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

        authService.refreshTokenIfNeeded();

        validateAuthentication();

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

    public MercadoLivreOrderDetail getOrderDetail(
            Long orderId
    ) {

        authService.refreshTokenIfNeeded();

        validateAuthentication();

        String accessToken =
                tokenStore.getToken()
                        .getAccess_token();

        return webClient.get()
                .uri(
                        "/orders/" + orderId
                )
                .header(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + accessToken
                )
                .retrieve()
                .bodyToMono(
                        MercadoLivreOrderDetail.class
                )
                .block();
    }

    private void validateAuthentication() {

        if (!tokenStore.hasToken()) {

            throw new RuntimeException(
                    "Mercado Livre não autenticado"
            );
        }
    }
}