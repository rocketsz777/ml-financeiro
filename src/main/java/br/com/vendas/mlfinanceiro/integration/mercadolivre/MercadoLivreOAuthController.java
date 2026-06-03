package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.service.marketplace.auth.MercadoLivreAuthService;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class MercadoLivreOAuthController {

    private final MercadoLivreAuthService authService;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    public MercadoLivreOAuthController(
            MercadoLivreAuthService authService
    ) {

        this.authService =
                authService;
    }

    @GetMapping("/oauth/mercadolivre/login")
    public String login() {

        return authService
                .generateAuthorizationUrl();
    }

    @GetMapping("/oauth/mercadolivre/callback")
    public RedirectView callback(
            @RequestParam("code") String code
    ) {

        try {

            authService.exchangeCodeForToken(
                    code
            );

            return redirectToApp(
                    "mercadolivre",
                    "success"
            );

        } catch (Exception ex) {

            return redirectToApp(
                    "mercadolivre",
                    "error"
            );
        }
    }

    private RedirectView redirectToApp(
            String marketplace,
            String status
    ) {

        return new RedirectView(
                frontendUrl
                        + "?auth="
                        + marketplace
                        + "&status="
                        + status
        );
    }
}
