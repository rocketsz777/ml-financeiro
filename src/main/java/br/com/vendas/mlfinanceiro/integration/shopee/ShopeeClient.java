package br.com.vendas.mlfinanceiro.integration.shopee;

import br.com.vendas.mlfinanceiro.domain.MarketplaceToken;
import br.com.vendas.mlfinanceiro.service.marketplace.auth.ShopeeAuthService;
import br.com.vendas.mlfinanceiro.service.marketplace.token.MarketplaceTokenService;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class ShopeeClient {

    @Value("${shopee.partner-id}")
    private String partnerId;

    @Value("${shopee.partner-key}")
    private String partnerKey;

    private final ShopeeAuthService authService;

    private final MarketplaceTokenService tokenService;

    private final WebClient webClient;

    public ShopeeClient(
            ShopeeAuthService authService,
            MarketplaceTokenService tokenService
    ) {

        this.authService =
                authService;

        this.tokenService =
                tokenService;

        this.webClient =
                WebClient.builder()
                        .baseUrl(
                                "https://partner.shopeemobile.com"
                        )
                        .build();
    }

    public JsonNode getOrderList(
            String cursor
    ) {

        String path =
                "/api/v2/order/get_order_list";

        Instant now =
                Instant.now();

        long timeTo =
                now.getEpochSecond();

        long timeFrom =
                now.minus(
                        14,
                        ChronoUnit.DAYS
                ).getEpochSecond();

        return webClient.get()
                .uri(uriBuilder ->
                        addSignedParams(
                                uriBuilder.path(path),
                                path
                        )
                                .queryParam(
                                        "time_range_field",
                                        "create_time"
                                )
                                .queryParam(
                                        "time_from",
                                        timeFrom
                                )
                                .queryParam(
                                        "time_to",
                                        timeTo
                                )
                                .queryParam(
                                        "page_size",
                                        50
                                )
                                .queryParam(
                                        "cursor",
                                        cursor == null
                                                ? ""
                                                : cursor
                                )
                                .build()
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    public JsonNode getOrderDetail(
            String orderSn
    ) {

        String path =
                "/api/v2/order/get_order_detail";

        return webClient.get()
                .uri(uriBuilder ->
                        addSignedParams(
                                uriBuilder.path(path),
                                path
                        )
                                .queryParam(
                                        "order_sn_list",
                                        orderSn
                                )
                                .queryParam(
                                        "response_optional_fields",
                                        "buyer_username,item_list,total_amount,create_time,pay_time"
                                )
                                .build()
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    private org.springframework.web.util.UriBuilder addSignedParams(
            org.springframework.web.util.UriBuilder uriBuilder,
            String path
    ) {

        String accessToken =
                authService.getValidAccessToken();

        MarketplaceToken token =
                tokenService.getByMarketplace(
                        "SHOPEE"
                );

        long timestamp =
                Instant.now()
                        .getEpochSecond();

        String shopId =
                token.getSellerId();

        String sign =
                generateSignature(
                        partnerId
                                + path
                                + timestamp
                                + accessToken
                                + shopId
                );

        return uriBuilder
                .queryParam(
                        "partner_id",
                        partnerId
                )
                .queryParam(
                        "timestamp",
                        timestamp
                )
                .queryParam(
                        "access_token",
                        accessToken
                )
                .queryParam(
                        "shop_id",
                        shopId
                )
                .queryParam(
                        "sign",
                        sign
                );
    }

    private String generateSignature(
            String baseString
    ) {

        try {

            Mac mac =
                    Mac.getInstance(
                            "HmacSHA256"
                    );

            SecretKeySpec secretKeySpec =
                    new SecretKeySpec(
                            partnerKey.getBytes(
                                    StandardCharsets.UTF_8
                            ),
                            "HmacSHA256"
                    );

            mac.init(
                    secretKeySpec
            );

            byte[] hash =
                    mac.doFinal(
                            baseString.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            StringBuilder hexString =
                    new StringBuilder();

            for (byte b : hash) {

                String hex =
                        Integer.toHexString(
                                0xff & b
                        );

                if (hex.length() == 1) {

                    hexString.append(
                            '0'
                    );
                }

                hexString.append(
                        hex
                );
            }

            return hexString.toString();

        } catch (Exception e) {

            throw new RuntimeException(
                    "Erro ao gerar assinatura Shopee",
                    e
            );
        }
    }

    public JsonNode getEscrowDetail(String orderSn) {

        String path = "/api/v2/payment/get_escrow_detail";

        return webClient.get()
                .uri(uriBuilder ->
                        addSignedParams(
                                uriBuilder.path(path),
                                path
                        )
                                .queryParam("order_sn", orderSn)
                                .build()
                )
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
