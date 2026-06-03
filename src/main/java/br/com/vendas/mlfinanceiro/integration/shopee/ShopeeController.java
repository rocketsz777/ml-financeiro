package br.com.vendas.mlfinanceiro.integration.shopee;

import br.com.vendas.mlfinanceiro.service.marketplace.auth.ShopeeAuthService;
import br.com.vendas.mlfinanceiro.service.marketplace.token.MarketplaceTokenService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shopee")
public class ShopeeController {

    private final ShopeeAuthService shopeeAuthService;

    private final MarketplaceTokenService tokenService;

    public ShopeeController(
            ShopeeAuthService shopeeAuthService,
            MarketplaceTokenService tokenService
    ) {

        this.shopeeAuthService =
                shopeeAuthService;

        this.tokenService =
                tokenService;
    }

    @GetMapping("/login")
    public String login() {

        return shopeeAuthService
                .generateAuthUrl();
    }

    @GetMapping("/callback")
    public String callback(

            @RequestParam String code,

            @RequestParam("shop_id")
            String shopId
    ) {

        shopeeAuthService
                .saveAuthorizationCode(
                        code,
                        shopId
                );

        return "Shopee conectada com sucesso";
    }

    @GetMapping("/status")
    public String status() {

        try {

            tokenService.getByMarketplace(
                    "SHOPEE"
            );

            return "CONECTADO";

        } catch (Exception e) {

            return "DESCONECTADO";
        }
    }
}