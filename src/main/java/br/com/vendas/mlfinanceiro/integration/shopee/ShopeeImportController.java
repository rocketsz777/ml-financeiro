package br.com.vendas.mlfinanceiro.integration.shopee;

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

        shopeeImportService
                .simulateImport();

        return "Pedidos Shopee importados";
    }
}