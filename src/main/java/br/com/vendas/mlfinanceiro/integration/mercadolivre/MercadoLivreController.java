package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.service.marketplace.auth.MercadoLivreAuthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MercadoLivreController {

    private final MercadoLivreClient client;

    private final MercadoLivreAuthService authService;

    public MercadoLivreController(
            MercadoLivreClient client,
            MercadoLivreAuthService authService
    ) {

        this.client =
                client;

        this.authService =
                authService;
    }

    @GetMapping("/api/mercadolivre/status")
    public String status() {

        try {

            authService.getValidAccessToken();

            return "CONECTADO";

        } catch (Exception ex) {

            return "DESCONECTADO";
        }
    }

    @GetMapping("/api/mercadolivre/me")
    public String me() {

        return client.getMyUserData();
    }
}
