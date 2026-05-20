package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.integration.MercadoLivreClient;
import br.com.vendas.mlfinanceiro.service.SalesService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@RestController
@RequestMapping("/api/webhook")
public class WebhookController {

    private final MercadoLivreClient mercadoLivreClient;
    private final SalesService salesService;

    public WebhookController(MercadoLivreClient mercadoLivreClient, SalesService salesService) {
        this.mercadoLivreClient = mercadoLivreClient;
        this.salesService = salesService;
    }

    @PostMapping("/mercadolivre")
    public ResponseEntity<Void> receber(@RequestBody JsonNode payload) {
        String topic = payload.path("topic").asText("");
        String resource = payload.path("resource").asText("");

        if (topic.contains("orders") || resource.contains("/orders/")) {
            String orderId = extractId(resource, payload);
            JsonNode order = mercadoLivreClient.getOrder(orderId);
            processOrder(order);
        }
        return ResponseEntity.ok().build();
    }

    private void processOrder(JsonNode order) {
        String orderId = order.path("id").asText();
        JsonNode items = order.path("order_items");
        if (!items.isArray()) return;

        for (JsonNode item : items) {
            JsonNode product = item.path("item");
            String sku = product.path("seller_custom_field").asText(product.path("id").asText());
            String productName = product.path("title").asText();
            int quantity = item.path("quantity").asInt(1);
            BigDecimal unitPrice = item.path("unit_price").decimalValue();

            BigDecimal fee = order.path("total_amount").isMissingNode() ? BigDecimal.ZERO : BigDecimal.ZERO;
            BigDecimal shipping = BigDecimal.ZERO;

            salesService.ingestMlOrder(orderId, sku, productName, quantity, unitPrice, fee, shipping);
        }
    }

    private String extractId(String resource, JsonNode payload) {
        if (resource != null && resource.matches(".*/orders/\d+")) {
            return resource.substring(resource.lastIndexOf('/') + 1);
        }
        return payload.path("id").asText("");
    }
}
