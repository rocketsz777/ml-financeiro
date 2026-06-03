package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.dto.ImportSalesResponse;
import br.com.vendas.mlfinanceiro.service.importer.ImportService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImportController {

    private final ImportService importService;

    public ImportController(
            ImportService importService
    ) {

        this.importService =
                importService;
    }

    @PostMapping("/api/import/sales")
    public ImportSalesResponse importSales() {

        return importService
                .importSales();
    }
}