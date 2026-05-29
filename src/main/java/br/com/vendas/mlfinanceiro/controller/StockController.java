package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.domain.StockMovement;
import br.com.vendas.mlfinanceiro.dto.StockEntryRequest;
import br.com.vendas.mlfinanceiro.service.stock.StockService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class StockController {

    private final StockService stockService;

    public StockController(
            StockService stockService
    ) {

        this.stockService = stockService;
    }

    @PostMapping("/entry")
    public void addStock(
            @RequestBody StockEntryRequest request
    ) {

        stockService.addStock(
                request
        );
    }

    @GetMapping("/movements")
    public List<StockMovement> movements() {

        return stockService
                .listMovements();
    }
}