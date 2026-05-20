package br.com.vendas.mlfinanceiro.integration;

import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class MercadoLivreClient {

    private final WebClient webClient;

    public MercadoLivreClient(@Value("${mercadolivre.access-token}") String accessToken) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.mercadolibre.com")
                .defaultHeader("Authorization", "Bearer " + accessToken)
                .build();
    }

    public JsonNode getOrder(String orderId) {
        return webClient.get()
                .uri("/orders/{id}", orderId)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
