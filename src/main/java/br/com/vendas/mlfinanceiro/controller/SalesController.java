package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.dto.MonthlySummaryResponse;
import br.com.vendas.mlfinanceiro.dto.SaleRequest;
import br.com.vendas.mlfinanceiro.service.ReportService;
import br.com.vendas.mlfinanceiro.service.SalesService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SalesController {

    private final SalesService salesService;
    private final ReportService reportService;

    public SalesController(SalesService salesService, ReportService reportService) {
        this.salesService = salesService;
        this.reportService = reportService;
    }

    @PostMapping("/vendas")
    public Sale registrarVenda(@RequestBody SaleRequest request) {
        return salesService.registerSale(request);
    }

    @GetMapping("/vendas")
    public List<Sale> listarVendas() {
        return salesService.listSales();
    }

    @GetMapping("/resumo/{mes}")
    public MonthlySummaryResponse resumo(@PathVariable String mes) {
        return reportService.monthlySummary(YearMonth.parse(mes));
    }

    @GetMapping("/fechamento/{mes}/excel")
    public ResponseEntity<byte[]> excel(@PathVariable String mes) throws Exception {
        Path path = reportService.generateExcel(YearMonth.parse(mes));
        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=" + path.getFileName())
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(Files.readAllBytes(path));
    }
}
