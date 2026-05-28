package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MercadoLivreOAuthController {

    private final MercadoLivreAuthService authService;

    private final MercadoLivreTokenStore tokenStore;

    public MercadoLivreOAuthController(
            MercadoLivreAuthService authService,
            MercadoLivreTokenStore tokenStore
    ) {

        this.authService =
                authService;

        this.tokenStore =
                tokenStore;
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

        MercadoLivreTokenResponse token =
                authService
                        .exchangeCodeForToken(
                                code
                        );

        tokenStore.save(
                token
        );

        return "Mercado Livre conectado com sucesso!";
    }
}