package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.dto.MonthlySummaryResponse;
import br.com.vendas.mlfinanceiro.dto.SaleRequest;
import br.com.vendas.mlfinanceiro.service.ReportService;
import br.com.vendas.mlfinanceiro.service.SalesService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.time.YearMonth;

@RestController
@RequestMapping("/api/sales")
public class SalesController {

    private final SalesService salesService;
    private final ReportService reportService;

    public SalesController(
            SalesService salesService,
            ReportService reportService
    ) {

        this.salesService = salesService;
        this.reportService = reportService;
    }

    @PostMapping
    public Sale registerSale(
            @RequestBody SaleRequest request
    ) {

        return salesService.registerSale(request);
    }

    @GetMapping("/summary")
    public MonthlySummaryResponse summary(
            @RequestParam int year,
            @RequestParam int month
    ) {

        return reportService.monthlySummary(
                YearMonth.of(year, month)
        );
    }

    @GetMapping("/report")
    public ResponseEntity<FileSystemResource> report(
            @RequestParam int year,
            @RequestParam int month
    ) {

        Path file =
                reportService.generateExcel(
                        YearMonth.of(year, month)
                );

        FileSystemResource resource =
                new FileSystemResource(file);

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" +
                                file.getFileName()
                )
                .contentType(
                        MediaType.APPLICATION_OCTET_STREAM
                )
                .body(resource);
    }
}