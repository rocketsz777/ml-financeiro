package br.com.vendas.mlfinanceiro.integration.shopee;

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

        shopeeImportService
                .simulateImport();

        return "Shopee order imported successfully";
    }
}