package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MercadoLivreImportController {

    private final MercadoLivreImportService importService;

    public MercadoLivreImportController(
            MercadoLivreImportService importService
    ) {

        this.importService =
                importService;
    }

    @GetMapping("/ml/import-orders")
    public String importOrders() {

        int imported =
                importService.importOrders();

        return "Pedidos importados: " +
                imported;
    }
}