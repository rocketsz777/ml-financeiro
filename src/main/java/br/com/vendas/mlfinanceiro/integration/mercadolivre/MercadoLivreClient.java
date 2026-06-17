package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.exception.BusinessException;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResponse;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreOrderDetail;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreShipmentResponse;
import br.com.vendas.mlfinanceiro.service.marketplace.auth.MercadoLivreAuthService;

import com.fasterxml.jackson.annotation.JsonProperty;
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
        this.authService = authService;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.mercadolibre.com")
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

    // Busca o ID do vendedor autenticado via /users/me
    private String getSellerId() {

        String accessToken =
                authService.getValidAccessToken();

        MercadoLivreUserResponse user =
                webClient.get()
                        .uri("/users/me")
                        .header(
                                HttpHeaders.AUTHORIZATION,
                                "Bearer " + accessToken
                        )
                        .retrieve()
                        .bodyToMono(
                                MercadoLivreUserResponse.class
                        )
                        .block();

        if (user == null || user.getId() == null) {
            throw new BusinessException(
                    "Não foi possível obter o ID do vendedor"
            );
        }

        System.out.println(
                "Seller ID obtido: " + user.getId()
        );

        return String.valueOf(user.getId());
    }

    public MercadoLivreOrderResponse getOrders() {

        validateAuthentication();

        String accessToken =
                authService.getValidAccessToken();

        // CORREÇÃO: o endpoint /orders/search exige o parâmetro
        // seller obrigatoriamente, caso contrário retorna vazio
        String sellerId = getSellerId();

        return webClient.get()
                .uri(uriBuilder ->
                        uriBuilder
                                .path("/orders/search")
                                .queryParam("seller", sellerId)
                                .queryParam("sort", "date_desc")
                                .queryParam("order.status", "paid")
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
    public MercadoLivreOrderResponse getOrders(
            int offset,
            int limit
    ) {

        validateAuthentication();

        String accessToken =
                authService.getValidAccessToken();

        String sellerId =
                getSellerId();

        return webClient.get()

                .uri(uriBuilder ->

                        uriBuilder

                                .path("/orders/search")

                                .queryParam("seller", sellerId)

                                .queryParam("sort", "date_desc")

                                .queryParam("order.status", "paid")

                                .queryParam(
                                        "offset",
                                        offset
                                )

                                .queryParam(
                                        "limit",
                                        limit
                                )

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

    // DTO interno para deserializar apenas o ID do /users/me
    static class MercadoLivreUserResponse {

        @JsonProperty("id")
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }

    public MercadoLivreShipmentResponse getShipmentById(Long shippingId) {
        validateAuthentication();

        String accessToken = authService.getValidAccessToken();

        return webClient.get()
                .uri("/shipments/" + shippingId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(MercadoLivreShipmentResponse.class)
                .block();
    }

}