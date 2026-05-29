package br.com.vendas.mlfinanceiro.service.marketplace.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@Service
public class ShopeeAuthService {

    @Value("${shopee.partner-id}")
    private String partnerId;

    @Value("${shopee.partner-key}")
    private String partnerKey;

    @Value("${shopee.redirect-uri}")
    private String redirectUri;

    public String generateAuthUrl() {

        long timestamp =
                Instant.now()
                        .getEpochSecond();

        String path =
                "/api/v2/shop/auth_partner";

        String baseString =
                partnerId +
                        path +
                        timestamp;

        String sign =
                generateSignature(
                        baseString
                );

        return "https://partner.shopeemobile.com"
                + path
                + "?partner_id=" + partnerId
                + "&timestamp=" + timestamp
                + "&sign=" + sign
                + "&redirect=" + redirectUri;
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