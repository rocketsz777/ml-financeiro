package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.service.marketplace.importer.MarketplaceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/marketplaces")
public class MarketplaceController {

    private final MarketplaceService marketplaceService;

    public MarketplaceController(
            MarketplaceService marketplaceService
    ) {

        this.marketplaceService =
                marketplaceService;
    }

    @GetMapping("/import/{marketplace}")
    public List<Sale> importSales(
            @PathVariable String marketplace
    ) {

        Marketplace mp;

        switch (marketplace.toLowerCase()) {

            case "ml":
            case "mercadolivre":
                mp = Marketplace.MERCADO_LIVRE;
                break;

            case "shopee":
                mp = Marketplace.SHOPEE;
                break;

            default:
                throw new IllegalArgumentException(
                        "Marketplace inválido"
                );
        }

        return marketplaceService.importSales(mp);
    }

    @GetMapping("/import/all")
    public List<Sale> importAllSales() {

        return marketplaceService.importAllSales();
    }
}