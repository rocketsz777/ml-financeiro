package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.service.marketplace.auth.MercadoLivreAuthService;
import br.com.vendas.mlfinanceiro.service.marketplace.importer.MercadoLivreImportService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MercadoLivreController {

    private final MercadoLivreClient client;

    private final MercadoLivreImportService importService;

    private final MercadoLivreAuthService authService;

    public MercadoLivreController(
            MercadoLivreClient client,
            MercadoLivreImportService importService,
            MercadoLivreAuthService authService
    ) {

        this.client =
                client;

        this.importService =
                importService;

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

    @PostMapping("/api/mercadolivre/import")
    public String importOrders() {

        int imported =
                importService.importOrders();

        return "Pedidos importados: "
                + imported;
    }
}