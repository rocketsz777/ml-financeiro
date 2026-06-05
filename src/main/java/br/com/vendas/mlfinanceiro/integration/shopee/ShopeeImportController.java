package br.com.vendas.mlfinanceiro.integration.shopee;

import br.com.vendas.mlfinanceiro.service.marketplace.importer.ShopeeImportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shopee")
public class ShopeeImportController {

    private final ShopeeImportService shopeeImportService;

    public ShopeeImportController(
            ShopeeImportService shopeeImportService
    ) {

        this.shopeeImportService =
                shopeeImportService;
    }

    @GetMapping("/import")
    public String importOrders() {

        int imported =
                shopeeImportService
                        .importOrders();

        return "Pedidos Shopee importados: "
                + imported;
    }
}
