package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.service.marketplace.auth.MercadoLivreAuthService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MercadoLivreOAuthController {

    private final MercadoLivreAuthService authService;

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
    public String callback(
            @RequestParam("code") String code
    ) {

        authService.exchangeCodeForToken(
                code
        );

        return "Mercado Livre conectado com sucesso!";
    }
}