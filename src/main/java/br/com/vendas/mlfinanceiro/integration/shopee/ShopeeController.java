package br.com.vendas.mlfinanceiro.integration.shopee;

import br.com.vendas.mlfinanceiro.service.marketplace.auth.ShopeeAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/shopee")
public class ShopeeController {

    private final ShopeeAuthService shopeeAuthService;

    public ShopeeController(
            ShopeeAuthService shopeeAuthService
    ) {

        this.shopeeAuthService =
                shopeeAuthService;
    }

    @GetMapping("/login")
    public String login() {

        return shopeeAuthService
                .generateAuthUrl();
    }

    @GetMapping("/callback")
    public String callback(
            @RequestParam String code,
            @RequestParam String shop_id
    ) {

        return "Shopee auth success. "
                + "Code: "
                + code
                + " ShopId: "
                + shop_id;
    }
}