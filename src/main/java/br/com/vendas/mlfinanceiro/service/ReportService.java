package br.com.vendas.mlfinanceiro.service;

import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.dto.MonthlySummaryResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    private final FileStoreService fileStoreService;

    public ReportService(FileStoreService fileStoreService) {
        this.fileStoreService = fileStoreService;
    }

    public MonthlySummaryResponse monthlySummary(YearMonth month) {

        List<Sale> sales = fileStoreService.loadSales().stream()
                .filter(s -> YearMonth.from(s.getSoldAt()).equals(month))
                .collect(Collectors.toList());

        BigDecimal totalRevenue = sales.stream()
                .map(Sale::grossRevenue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCost = sales.stream()
                .map(Sale::getProductCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFees = sales.stream()
                .map(Sale::getMarketplaceFee)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalShipping = sales.stream()
                .map(Sale::getShippingCost)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProfit = sales.stream()
                .map(Sale::getProfit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, List<Sale>> grouped = sales.stream()
                .collect(Collectors.groupingBy(Sale::getSku));

        List<MonthlySummaryResponse.ProductSummary> products = new ArrayList<>();

        for (Map.Entry<String, List<Sale>> e : grouped.entrySet()) {

            List<Sale> list = e.getValue();

            MonthlySummaryResponse.ProductSummary item =
                    new MonthlySummaryResponse.ProductSummary();

            item.setSku(e.getKey());
            item.setProductName(list.get(0).getProductName());

            item.setQuantitySold(
                    list.stream()
                            .mapToInt(Sale::getQuantity)
                            .sum()
            );

            item.setRevenue(
                    list.stream()
                            .map(Sale::grossRevenue)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );

            item.setCost(
                    list.stream()
                            .map(Sale::getProductCost)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );

            item.setProfit(
                    list.stream()
                            .map(Sale::getProfit)
                            .reduce(BigDecimal.ZERO, BigDecimal::add)
            );

            products.add(item);
        }

        MonthlySummaryResponse response = new MonthlySummaryResponse();

        response.setMonth(month.toString());
        response.setTotalRevenue(totalRevenue);
        response.setTotalCost(totalCost);
        response.setTotalFees(totalFees);
        response.setTotalShipping(totalShipping);
        response.setTotalProfit(totalProfit);
        response.setProducts(products);

        return response;
    }

    public Path generateExcel(YearMonth month) {

        MonthlySummaryResponse summary = monthlySummary(month);

        Path output = fileStoreService.getDataDir()
                .resolve("fechamento-" + month + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet resumo = workbook.createSheet("Resumo");
            Sheet vendas = workbook.createSheet("Vendas");
            Sheet produtos = workbook.createSheet("Produtos");

            CellStyle headerStyle = createHeaderStyle(workbook);

            // =========================
            // ABA RESUMO
            // =========================

            int r = 0;

            Row h = resumo.createRow(r++);

            createHeaderCell(h, 0, "Mês", headerStyle);
            createHeaderCell(h, 1, "Faturamento", headerStyle);
            createHeaderCell(h, 2, "Custos", headerStyle);
            createHeaderCell(h, 3, "Taxas ML", headerStyle);
            createHeaderCell(h, 4, "Frete", headerStyle);
            createHeaderCell(h, 5, "Lucro", headerStyle);

            Row row = resumo.createRow(r++);

            row.createCell(0).setCellValue(summary.getMonth());
            row.createCell(1).setCellValue(summary.getTotalRevenue().doubleValue());
            row.createCell(2).setCellValue(summary.getTotalCost().doubleValue());
            row.createCell(3).setCellValue(summary.getTotalFees().doubleValue());
            row.createCell(4).setCellValue(summary.getTotalShipping().doubleValue());
            row.createCell(5).setCellValue(summary.getTotalProfit().doubleValue());

            // =========================
            // ABA VENDAS
            // =========================

            int rv = 0;

            Row hv = vendas.createRow(rv++);

            String[] saleHeaders = {
                    "Data",
                    "OrderId",
                    "SKU",
                    "Produto",
                    "Qtd",
                    "Venda Unit.",
                    "Custo Total",
                    "Taxa ML",
                    "Frete",
                    "Lucro"
            };

            for (int i = 0; i < saleHeaders.length; i++) {
                createHeaderCell(hv, i, saleHeaders[i], headerStyle);
            }

            List<Sale> filteredSales = fileStoreService.loadSales().stream()
                    .filter(s -> YearMonth.from(s.getSoldAt()).equals(month))
                    .collect(Collectors.toList());

            for (Sale s : filteredSales) {

                Row sr = vendas.createRow(rv++);

                sr.createCell(0).setCellValue(s.getSoldAt().toString());
                sr.createCell(1).setCellValue(s.getOrderId());
                sr.createCell(2).setCellValue(s.getSku());
                sr.createCell(3).setCellValue(s.getProductName());
                sr.createCell(4).setCellValue(s.getQuantity());
                sr.createCell(5).setCellValue(s.getUnitSalePrice().doubleValue());
                sr.createCell(6).setCellValue(s.getProductCost().doubleValue());
                sr.createCell(7).setCellValue(s.getMarketplaceFee().doubleValue());
                sr.createCell(8).setCellValue(s.getShippingCost().doubleValue());
                sr.createCell(9).setCellValue(s.getProfit().doubleValue());
            }

            // =========================
            // ABA PRODUTOS
            // =========================

            int rp = 0;

            Row hp = produtos.createRow(rp++);

            String[] prodHeaders = {
                    "SKU",
                    "Produto",
                    "Vendidos",
                    "Receita",
                    "Custo",
                    "Lucro"
            };

            for (int i = 0; i < prodHeaders.length; i++) {
                createHeaderCell(hp, i, prodHeaders[i], headerStyle);
            }

            for (MonthlySummaryResponse.ProductSummary p : summary.getProducts()) {

                Row pr = produtos.createRow(rp++);

                pr.createCell(0).setCellValue(p.getSku());
                pr.createCell(1).setCellValue(p.getProductName());
                pr.createCell(2).setCellValue(p.getQuantitySold());
                pr.createCell(3).setCellValue(p.getRevenue().doubleValue());
                pr.createCell(4).setCellValue(p.getCost().doubleValue());
                pr.createCell(5).setCellValue(p.getProfit().doubleValue());
            }

            autoSize(resumo, 6);
            autoSize(vendas, 10);
            autoSize(produtos, 6);

            try (OutputStream os = Files.newOutputStream(output)) {
                workbook.write(os);
            }

            return output;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel", e);
        }
    }

    private CellStyle createHeaderStyle(Workbook workbook) {

        Font font = workbook.createFont();
        font.setBold(true);

        CellStyle style = workbook.createCellStyle();
        style.setFont(font);

        return style;
    }

    private void createHeaderCell(
            Row row,
            int col,
            String value,
            CellStyle style
    ) {

        Cell cell = row.createCell(col);

        cell.setCellValue(value);
        cell.setCellStyle(style);
    }

    private void autoSize(Sheet sheet, int columns) {

        for (int i = 0; i < columns; i++) {
            sheet.autoSizeColumn(i);
        }
    }
}