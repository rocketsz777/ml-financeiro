package br.com.vendas.mlfinanceiro.service.report;

import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.dto.MonthlySummaryResponse;
import br.com.vendas.mlfinanceiro.repository.SaleRepository;
import br.com.vendas.mlfinanceiro.service.file.FileStoreService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ReportService {

    // CONSTANTE
    private static final String LUCRO_LABEL = "Lucro";

    private final FileStoreService fileStoreService;
    private final SaleRepository saleRepository;

    public ReportService(FileStoreService fileStoreService, SaleRepository saleRepository) {
        this.fileStoreService = fileStoreService;
        this.saleRepository = saleRepository;
    }

    public MonthlySummaryResponse monthlySummary(YearMonth month) {
        List<Sale> sales = saleRepository.findAll()
                .stream()
                .filter(s -> YearMonth.from(s.getSoldAt()).equals(month))
                .collect(Collectors.toList());

        BigDecimal totalRevenue = sales.stream().map(Sale::getGrossAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCost = sales.stream().map(Sale::getProductCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalFees = sales.stream().map(Sale::getMarketplaceFee).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalShipping = sales.stream().map(Sale::getShippingCost).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProfit = sales.stream().map(Sale::getProfit).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, List<Sale>> grouped = sales.stream().collect(Collectors.groupingBy(Sale::getSku));
        List<MonthlySummaryResponse.ProductSummary> products = new ArrayList<>();

        for (Map.Entry<String, List<Sale>> e : grouped.entrySet()) {
            List<Sale> list = e.getValue();
            MonthlySummaryResponse.ProductSummary item = new MonthlySummaryResponse.ProductSummary();
            item.setSku(e.getKey());
            item.setProductName(list.get(0).getProductName());
            item.setQuantitySold(list.stream().mapToInt(Sale::getQuantity).sum());
            item.setRevenue(list.stream().map(Sale::getGrossAmount).reduce(BigDecimal.ZERO, BigDecimal::add));
            item.setCost(list.stream().map(Sale::getProductCost).reduce(BigDecimal.ZERO, BigDecimal::add));
            item.setProfit(list.stream().map(Sale::getProfit).reduce(BigDecimal.ZERO, BigDecimal::add));
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

        List<Sale> sales = saleRepository.findAll()
                .stream()
                .filter(s -> YearMonth.from(s.getSoldAt()).equals(month))
                .collect(Collectors.toList());

        Path output = fileStoreService.getDataDir()
                .resolve("fechamento-" + month + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = createHeaderStyle(workbook);

            // Chamada dos métodos extraídos para preenchimento individualizado
            fillResumoSheet(workbook, summary, sales, headerStyle);
            fillVendasSheet(workbook, sales, headerStyle);
            fillProdutosSheet(workbook, summary, headerStyle);

            try (OutputStream os = Files.newOutputStream(output)) {
                workbook.write(os);
            }

            return output;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel", e);
        }
    }

    private void fillResumoSheet(Workbook workbook, MonthlySummaryResponse summary, List<Sale> sales, CellStyle headerStyle) {
        Sheet resumo = workbook.createSheet("Resumo");

        int totalOrders = sales.size();
        int totalUnits = sales.stream().mapToInt(Sale::getQuantity).sum();
        BigDecimal averageTicket = totalOrders > 0
                ? summary.getTotalRevenue().divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        // Criando as linhas com índices fixos (0 e 1), eliminando a variável 'r'
        Row h = resumo.createRow(0);

        createHeaderCell(h, 0, "Mês", headerStyle);
        createHeaderCell(h, 1, "Faturamento", headerStyle);
        createHeaderCell(h, 2, "Custos", headerStyle);
        createHeaderCell(h, 3, "Taxas", headerStyle);
        createHeaderCell(h, 4, "Frete", headerStyle);
        createHeaderCell(h, 5, LUCRO_LABEL, headerStyle);
        createHeaderCell(h, 6, "Pedidos", headerStyle);
        createHeaderCell(h, 7, "Unidades Vendidas", headerStyle);
        createHeaderCell(h, 8, "Ticket Médio", headerStyle);

        Row row = resumo.createRow(1);
        row.createCell(0).setCellValue(summary.getMonth());
        row.createCell(1).setCellValue(summary.getTotalRevenue().doubleValue());
        row.createCell(2).setCellValue(summary.getTotalCost().doubleValue());
        row.createCell(3).setCellValue(summary.getTotalFees().doubleValue());
        row.createCell(4).setCellValue(summary.getTotalShipping().doubleValue());
        row.createCell(5).setCellValue(summary.getTotalProfit().doubleValue());
        row.createCell(6).setCellValue(totalOrders);
        row.createCell(7).setCellValue(totalUnits);
        row.createCell(8).setCellValue(averageTicket.doubleValue());

        autoSize(resumo, 9);
    }

    private void fillVendasSheet(Workbook workbook, List<Sale> sales, CellStyle headerStyle) {
        Sheet vendas = workbook.createSheet("Vendas");
        int vr = 0;
        Row vh = vendas.createRow(vr++);

        createHeaderCell(vh, 0, "Data", headerStyle);
        createHeaderCell(vh, 1, "Marketplace", headerStyle);
        createHeaderCell(vh, 2, "Pedido", headerStyle);
        createHeaderCell(vh, 3, "Produto", headerStyle);
        createHeaderCell(vh, 4, "SKU", headerStyle);
        createHeaderCell(vh, 5, "Anúncio", headerStyle);
        createHeaderCell(vh, 6, "Qtd", headerStyle);
        createHeaderCell(vh, 7, "Faturamento", headerStyle);
        createHeaderCell(vh, 8, "Custo", headerStyle);
        createHeaderCell(vh, 9, LUCRO_LABEL, headerStyle);

        for (Sale sale : sales) {
            Row rowVenda = vendas.createRow(vr++);

            rowVenda.createCell(0).setCellValue(sale.getSoldAt() != null ? sale.getSoldAt().toString() : "");
            rowVenda.createCell(1).setCellValue(sale.getMarketplace() != null ? sale.getMarketplace().name() : "");
            rowVenda.createCell(2).setCellValue(sale.getOrderId());
            rowVenda.createCell(3).setCellValue(sale.getProductName());
            rowVenda.createCell(4).setCellValue(sale.getSku());
            rowVenda.createCell(5).setCellValue(sale.getMarketplaceItemId());
            rowVenda.createCell(6).setCellValue(sale.getQuantity());
            rowVenda.createCell(7).setCellValue(sale.getNetAmount() != null ? sale.getNetAmount().doubleValue() : 0.0);
            rowVenda.createCell(8).setCellValue(sale.getProductCost() != null ? sale.getProductCost().multiply(BigDecimal.valueOf(sale.getQuantity())).doubleValue() : 0.0);
            rowVenda.createCell(9).setCellValue(sale.getProfit() != null ? sale.getProfit().doubleValue() : 0.0);
        }

        autoSize(vendas, 10);
    }

    private void fillProdutosSheet(Workbook workbook, MonthlySummaryResponse summary, CellStyle headerStyle) {
        Sheet produtos = workbook.createSheet("Produtos");
        int pr = 0;
        Row ph = produtos.createRow(pr++);

        createHeaderCell(ph, 0, "Produto", headerStyle);
        createHeaderCell(ph, 1, "SKU", headerStyle);
        createHeaderCell(ph, 2, "Vendidas", headerStyle);
        createHeaderCell(ph, 3, "Receita", headerStyle);
        createHeaderCell(ph, 4, "Custo", headerStyle);
        createHeaderCell(ph, 5, LUCRO_LABEL, headerStyle);

        for (MonthlySummaryResponse.ProductSummary p : summary.getProducts()) {
            Row rowProduto = produtos.createRow(pr++);

            rowProduto.createCell(0).setCellValue(p.getProductName());
            rowProduto.createCell(1).setCellValue(p.getSku());
            rowProduto.createCell(2).setCellValue(p.getQuantitySold());
            rowProduto.createCell(3).setCellValue(p.getRevenue() != null ? p.getRevenue().doubleValue() : 0.0);
            rowProduto.createCell(4).setCellValue(p.getCost() != null ? p.getCost().doubleValue() : 0.0);
            rowProduto.createCell(5).setCellValue(p.getProfit() != null ? p.getProfit().doubleValue() : 0.0);
        }

        autoSize(produtos, 6);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private void createHeaderCell(Row row, int col, String value, CellStyle style) {
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