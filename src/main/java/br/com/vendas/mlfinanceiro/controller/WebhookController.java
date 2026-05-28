package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.service.SalesService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/webhook")
public class WebhookController {

    private final SalesService salesService;

    public WebhookController(SalesService salesService) {
        this.salesService = salesService;
    }

    @PostMapping("/mercadolivre")
    public ResponseEntity<String> mercadoLivreWebhook(
            @RequestBody JsonNode payload
    ) {

        System.out.println(
                "Webhook recebido Mercado Livre:"
        );

        System.out.println(payload);

        try {

            processOrder(payload);

            return ResponseEntity.ok(
                    "Webhook processado"
            );

        } catch (Exception e) {

            e.printStackTrace();

            return ResponseEntity.internalServerError()
                    .body("Erro ao processar webhook");
        }
    }

    private void processOrder(JsonNode payload) {

        String resource =
                payload.path("resource")
                        .asText("");

        String orderId =
                extractId(resource, payload);

        JsonNode order =
                payload.path("order");

        JsonNode items =
                order.path("order_items");

        if (!items.isArray() || items.size() == 0) {

            System.out.println(
                    "Pedido sem itens"
            );

            return;
        }

        for (JsonNode item : items) {

            JsonNode product =
                    item.path("item");

            String sku =
                    product.path("seller_sku")
                            .asText("");

            String productName =
                    product.path("title")
                            .asText("");

            int quantity =
                    item.path("quantity")
                            .asInt(1);

            BigDecimal unitPrice =
                    item.path("unit_price")
                            .decimalValue();

            BigDecimal fee =
                    BigDecimal.ZERO;

            BigDecimal shipping =
                    BigDecimal.ZERO;

            salesService.ingestMLOrder(
                    orderId,
                    sku,
                    productName,
                    quantity,
                    unitPrice,
                    fee,
                    shipping
            );
        }
    }

    private String extractId(
            String resource,
            JsonNode payload
    ) {

        if (
                resource != null &&
                        resource.matches(".*/orders/\\\\d+")
        ) {

            return resource.substring(
                    resource.lastIndexOf('/') + 1
            );
        }

        return payload.path("id")
                .asText("");
    }
}