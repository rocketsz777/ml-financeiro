package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MercadoLivreController {

    private final MercadoLivreClient client;

    private final MercadoLivreImportService importService;

    private final MercadoLivreTokenStore tokenStore;

    public MercadoLivreController(
            MercadoLivreClient client,
            MercadoLivreImportService importService,
            MercadoLivreTokenStore tokenStore
    ) {

        this.client = client;

        this.importService = importService;

        this.tokenStore = tokenStore;
    }

    @GetMapping("/api/mercadolivre/status")
    public String status() {

        return tokenStore.hasToken()

                ?

                "CONECTADO"

                :

                "DESCONECTADO";
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