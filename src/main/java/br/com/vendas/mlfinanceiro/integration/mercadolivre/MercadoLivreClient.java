package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.exception.BusinessException;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResponse;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreOrderDetail;
import br.com.vendas.mlfinanceiro.service.marketplace.auth.MercadoLivreAuthService;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MercadoLivreClient {

    private final MercadoLivreAuthService authService;

    private final WebClient webClient;

    public MercadoLivreClient(
            MercadoLivreAuthService authService
    ) {

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

        validateAuthentication();

        String accessToken =
                authService.getValidAccessToken();

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

        validateAuthentication();

        String accessToken =
                authService.getValidAccessToken();

        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/orders/search")
                                .queryParam("sort", "date_desc")
                                .build()
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

    public MercadoLivreOrderDetail getOrderById(
            Long orderId
    ) {

        validateAuthentication();

        String accessToken =
                authService.getValidAccessToken();

        return webClient.get()
                .uri("/orders/" + orderId)
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

        try {

            authService.getValidAccessToken();

        } catch (Exception ex) {

            throw new BusinessException(
                    "Mercado Livre não autenticado"
            );
        }
    }
}