package br.com.vendas.mlfinanceiro.integration.shopee;

import br.com.vendas.mlfinanceiro.service.marketplace.auth.ShopeeAuthService;
import br.com.vendas.mlfinanceiro.service.marketplace.token.MarketplaceTokenService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
@RequestMapping("/shopee")
public class ShopeeController {

    private final ShopeeAuthService shopeeAuthService;

    private final MarketplaceTokenService tokenService;

    // CORREÇÃO: valor padrão para evitar falha de startup
    // caso a propriedade não esteja configurada
    @Value("${app.frontend-url:http://localhost:8080}")
    private String frontendUrl;

    public ShopeeController(
            ShopeeAuthService shopeeAuthService,
            MarketplaceTokenService tokenService
    ) {
        this.shopeeAuthService = shopeeAuthService;
        this.tokenService = tokenService;
    }

    @GetMapping("/login")
    public String login() {
        return shopeeAuthService.generateAuthUrl();
    }

    @GetMapping("/callback")
    public RedirectView callback(
            @RequestParam String code,
            @RequestParam("shop_id") String shopId
    ) {

        try {

            shopeeAuthService.exchangeCodeForToken(
                    code,
                    shopId
            );

            System.out.println(
                    "=== SHOPEE AUTH === Token salvo com sucesso! Shop ID: "
                            + shopId
            );

            return redirectToApp("shopee", "success");

        } catch (Exception ex) {

            // CORREÇÃO: log completo do erro para diagnóstico
            System.err.println(
                    "=== SHOPEE AUTH ERROR === "
                            + ex.getMessage()
            );

            ex.printStackTrace();

            return redirectToApp("shopee", "error");
        }
    }

    // Endpoint de diagnóstico — útil para testar sem OAuth
    // Remover em produção após validar a integração
    @GetMapping("/debug-token")
    public String debugToken() {

        try {

            ShopeeTokenResponse token =
                    shopeeAuthService.exchangeCodeForToken(
                            "TEST_CODE",
                            "TEST_SHOP_ID"
                    );

            return "Token recebido: "
                    + token.getAccess_token();

        } catch (Exception e) {

            return "Erro: " + e.getMessage();
        }
    }

    private RedirectView redirectToApp(
            String marketplace,
            String status
    ) {
        return new RedirectView(
                frontendUrl
                        + "?auth=" + marketplace
                        + "&status=" + status
        );
    }

    @GetMapping("/status")
    public String status() {

        try {

            tokenService.getByMarketplace("SHOPEE");
            return "CONECTADO";

        } catch (Exception e) {

            return "DESCONECTADO";
        }
    }
}