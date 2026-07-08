package br.com.vendas.mlfinanceiro.service.marketplace.auth;

import br.com.vendas.mlfinanceiro.domain.MarketplaceToken;
import br.com.vendas.mlfinanceiro.exception.BusinessException;
import br.com.vendas.mlfinanceiro.integration.shopee.ShopeeTokenResponse;
import br.com.vendas.mlfinanceiro.service.marketplace.token.MarketplaceTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.io.UnsupportedEncodingException;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ShopeeAuthService {

    // CONSTANTES
    private static final String MARKETPLACE_NAME = "SHOPEE";
    private static final String PARTNER_ID_PARAM = "partner_id";

    @Value("${shopee.partner-id}")
    private String partnerId;

    @Value("${shopee.partner-key}")
    private String partnerKey;

    @Value("${shopee.redirect-uri}")
    private String redirectUri;

    private final MarketplaceTokenService tokenService;

    private final WebClient webClient;

    public ShopeeAuthService(
            MarketplaceTokenService tokenService
    ) {

        this.tokenService =
                tokenService;

        this.webClient =
                WebClient.builder()
                        .baseUrl(
                                "https://partner.shopeemobile.com"
                        )
                        .build();
    }

    public String generateAuthUrl() {

        long timestamp =
                Instant.now()
                        .getEpochSecond();

        String path =
                "/api/v2/shop/auth_partner";

        String baseString =
                partnerId
                        + path
                        + timestamp;

        String sign =
                generateSignature(
                        baseString
                );

        return "https://partner.shopeemobile.com"
                + path
                + "?partner_id=" + partnerId
                + "&timestamp=" + timestamp
                + "&sign=" + sign
                + "&redirect="
                + encodeRedirectUri();
    }

    public ShopeeTokenResponse exchangeCodeForToken(
            String code,
            String shopId
    ) {

        String path =
                "/api/v2/auth/token/get";

        long timestamp =
                Instant.now()
                        .getEpochSecond();

        String sign =
                generateSignature(
                        partnerId
                                + path
                                + timestamp
                );

        Map<String, Object> requestBody =
                new LinkedHashMap<>();

        requestBody.put(
                "code",
                code
        );

        requestBody.put(
                "shop_id",
                Long.valueOf(shopId)
        );

        requestBody.put(
                PARTNER_ID_PARAM,
                Long.valueOf(partnerId)
        );

        ShopeeTokenResponse newToken =
                webClient.post()
                        .uri(uriBuilder ->
                                uriBuilder
                                        .path(path)
                                        .queryParam(
                                                PARTNER_ID_PARAM,
                                                partnerId
                                        )
                                        .queryParam(
                                                "timestamp",
                                                timestamp
                                        )
                                        .queryParam(
                                                "sign",
                                                sign
                                        )
                                        .build()
                        )
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(
                                ShopeeTokenResponse.class
                        )
                        .block();

        if (newToken == null
                || newToken.getAccess_token() == null) {

            throw new BusinessException(
                    getTokenErrorMessage(
                            newToken,
                            "Erro ao autenticar Shopee"
                    )
            );
        }

        tokenService.save(
                MARKETPLACE_NAME,
                String.valueOf(
                        newToken.getShop_id() != null
                                ? newToken.getShop_id()
                                : Long.valueOf(shopId)
                ),
                newToken.getAccess_token(),
                newToken.getRefresh_token(),
                getExpiresIn(newToken)
        );

        return newToken;
    }

    public String getValidAccessToken() {

        MarketplaceToken token =
                tokenService.getByMarketplace(
                        MARKETPLACE_NAME
                );

        if (tokenService.isExpired(token)) {

            refreshAccessToken();
        }

        return tokenService
                .getByMarketplace(
                        MARKETPLACE_NAME
                )
                .getAccessToken();
    }

    public void refreshAccessToken() {

        MarketplaceToken currentToken =
                tokenService.getByMarketplace(
                        MARKETPLACE_NAME
                );

        String path =
                "/api/v2/auth/access_token/get";

        long timestamp =
                Instant.now()
                        .getEpochSecond();

        String sign =
                generateSignature(
                        partnerId
                                + path
                                + timestamp
                );

        Map<String, Object> requestBody =
                new LinkedHashMap<>();

        requestBody.put(
                "refresh_token",
                currentToken.getRefreshToken()
        );

        requestBody.put(
                "shop_id",
                Long.valueOf(
                        currentToken.getSellerId()
                )
        );

        requestBody.put(
                PARTNER_ID_PARAM,
                Long.valueOf(partnerId)
        );

        ShopeeTokenResponse newToken =
                webClient.post()
                        .uri(uriBuilder ->
                                uriBuilder
                                        .path(path)
                                        .queryParam(
                                                PARTNER_ID_PARAM,
                                                partnerId
                                        )
                                        .queryParam(
                                                "timestamp",
                                                timestamp
                                        )
                                        .queryParam(
                                                "sign",
                                                sign
                                        )
                                        .build()
                        )
                        .bodyValue(requestBody)
                        .retrieve()
                        .bodyToMono(
                                ShopeeTokenResponse.class
                        )
                        .block();

        if (newToken == null
                || newToken.getAccess_token() == null) {

            throw new BusinessException(
                    getTokenErrorMessage(
                            newToken,
                            "Erro ao atualizar token Shopee"
                    )
            );
        }

        tokenService.save(
                MARKETPLACE_NAME,
                currentToken.getSellerId(),
                newToken.getAccess_token(),
                newToken.getRefresh_token(),
                getExpiresIn(newToken)
        );
    }

    private Long getExpiresIn(
            ShopeeTokenResponse token
    ) {

        return token.getExpire_in() != null
                ? token.getExpire_in()
                : 14400L;
    }

    private String getTokenErrorMessage(
            ShopeeTokenResponse token,
            String fallback
    ) {

        if (token == null) {

            return fallback;
        }

        if (token.getMessage() != null
                && !token.getMessage().trim().isEmpty()) {

            return fallback
                    + ": "
                    + token.getMessage();
        }

        if (token.getError() != null
                && !token.getError().trim().isEmpty()) {

            return fallback
                    + ": "
                    + token.getError();
        }

        return fallback;
    }

    private String encodeRedirectUri() {

        try {

            return URLEncoder.encode(
                    redirectUri,
                    StandardCharsets.UTF_8.name()
            );

        } catch (UnsupportedEncodingException e) {

            throw new RuntimeException(
                    "Erro ao codificar URL de retorno Shopee",
                    e
            );
        }
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
}