package br.com.vendas.mlfinanceiro.integration.shopee;

import br.com.vendas.mlfinanceiro.service.marketplace.importer.ShopeeImportService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/shopee")
public class ShopeeApiController {

    private final ShopeeImportService shopeeImportService;

    public ShopeeApiController(
            ShopeeImportService shopeeImportService
    ) {

        this.shopeeImportService =
                shopeeImportService;
    }

    @GetMapping("/simulate-import")
    public String simulateImport() {

        int imported =
                shopeeImportService
                        .importOrders();

        return "Shopee orders imported: "
                + imported;
    }
}
