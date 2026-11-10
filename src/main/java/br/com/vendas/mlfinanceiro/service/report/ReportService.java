package br.com.vendas.mlfinanceiro.service.report;

import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.domain.ExtraCost;
import br.com.vendas.mlfinanceiro.dto.MonthlySummaryResponse;
import br.com.vendas.mlfinanceiro.repository.ExtraCostRepository;
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
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
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
    private final ExtraCostRepository extraCostRepository;

    public ReportService(FileStoreService fileStoreService, SaleRepository saleRepository, ExtraCostRepository extraCostRepository) {
        this.fileStoreService = fileStoreService;
        this.saleRepository = saleRepository;
        this.extraCostRepository = extraCostRepository;
    }

    public MonthlySummaryResponse monthlySummary(YearMonth month) {
        List<Sale> sales = salesForMonth(month);

        BigDecimal totalRevenue = sales.stream().map(s -> s.getNetAmount() != null ? s.getNetAmount() : nz(s.getGrossAmount())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCost = sales.stream().map(s -> nz(s.getProductCost()).multiply(BigDecimal.valueOf(s.getQuantity() == null ? 0 : s.getQuantity())).add(nz(s.getExtraCosts()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalFees = sales.stream().map(s -> nz(s.getMarketplaceFee())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalShipping = sales.stream().map(s -> nz(s.getShippingCost())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalProfit = sales.stream().map(s -> nz(s.getProfit())).reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<String, List<Sale>> grouped = sales.stream().collect(Collectors.groupingBy(s -> s.getSku() == null ? "SEM SKU" : s.getSku()));
        List<MonthlySummaryResponse.ProductSummary> products = new ArrayList<>();

        for (Map.Entry<String, List<Sale>> e : grouped.entrySet()) {
            List<Sale> list = e.getValue();
            MonthlySummaryResponse.ProductSummary item = new MonthlySummaryResponse.ProductSummary();
            item.setSku(e.getKey());
            item.setProductName(list.get(0).getProductName());
            item.setQuantitySold(list.stream().mapToInt(s -> s.getQuantity() == null ? 0 : s.getQuantity()).sum());
            item.setRevenue(list.stream().map(s -> s.getNetAmount() != null ? s.getNetAmount() : nz(s.getGrossAmount())).reduce(BigDecimal.ZERO, BigDecimal::add));
            item.setCost(list.stream().map(s -> nz(s.getProductCost()).multiply(BigDecimal.valueOf(s.getQuantity() == null ? 0 : s.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add));
            item.setProfit(list.stream().map(s -> nz(s.getProfit())).reduce(BigDecimal.ZERO, BigDecimal::add));
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

        List<Sale> sales = salesForMonth(month);

        Path output = fileStoreService.getDataDir()
                .resolve("fechamento-" + month + ".xlsx");

        try (Workbook workbook = new XSSFWorkbook()) {
            CellStyle headerStyle = createHeaderStyle(workbook);
            CellStyle currencyStyle = createCurrencyStyle(workbook);
            List<ExtraCost> manualCosts = extraCostRepository.findByActiveTrue();

            fillResumoSheet(workbook, summary, sales, manualCosts, headerStyle, currencyStyle);
            fillVendasSheet(workbook, sales, headerStyle, currencyStyle);
            fillProdutosSheet(workbook, summary, sales, headerStyle, currencyStyle);
            fillManualCostsSheet(workbook, manualCosts, headerStyle, currencyStyle);

            try (OutputStream os = Files.newOutputStream(output)) {
                workbook.write(os);
            }

            return output;

        } catch (IOException e) {
            throw new RuntimeException("Erro ao gerar Excel", e);
        }
    }

    private List<Sale> salesForMonth(YearMonth month) {
        return saleRepository.findAll().stream()
                .filter(s -> s.getSoldAt() != null && YearMonth.from(s.getSoldAt()).equals(month))
                .sorted(Comparator.comparing(Sale::getSoldAt))
                .collect(Collectors.toList());
    }

    private void fillResumoSheet(Workbook workbook, MonthlySummaryResponse summary, List<Sale> sales, List<ExtraCost> manualCosts, CellStyle headerStyle, CellStyle currencyStyle) {
        Sheet resumo = workbook.createSheet("Resumo");

        BigDecimal productCost = sales.stream().map(s -> nz(s.getProductCost()).multiply(BigDecimal.valueOf(s.getQuantity() == null ? 0 : s.getQuantity()))).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal saleExtraCosts = sales.stream().map(s -> nz(s.getExtraCosts())).reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal manualExtraCosts = manualCosts.stream().map(c -> nz(c.getValue())).reduce(BigDecimal.ZERO, BigDecimal::add);
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        int totalOrders = sales.size();
        int totalUnits = sales.stream().mapToInt(s -> s.getQuantity() == null ? 0 : s.getQuantity()).sum();
        BigDecimal averageTicket = totalOrders > 0
                ? nz(summary.getTotalRevenue()).divide(BigDecimal.valueOf(totalOrders), 2, BigDecimal.ROUND_HALF_UP)
                : BigDecimal.ZERO;

        // Criando as linhas com índices fixos (0 e 1), eliminando a variável 'r'
        Row h = resumo.createRow(0);

        String[] headers = {"Mês", "Primeira venda", "Última venda", "Faturamento líquido", "Custo produtos vendidos", "Extras por venda", "Extras manuais cadastrados", "Taxas marketplace", "Frete", "Lucro registrado", "Lucro após extras manuais", "Custo total do mês", "Vendas", "Unidades vendidas", "Ticket médio"};
        for (int i = 0; i < headers.length; i++) createHeaderCell(h, i, headers[i], headerStyle);

        Row row = resumo.createRow(1);
        row.createCell(0).setCellValue(summary.getMonth());
        row.createCell(1).setCellValue(sales.isEmpty() ? "Sem vendas" : sales.get(0).getSoldAt().format(dateFormat));
        row.createCell(2).setCellValue(sales.isEmpty() ? "Sem vendas" : sales.get(sales.size() - 1).getSoldAt().format(dateFormat));
        setCurrency(row.createCell(3), summary.getTotalRevenue(), currencyStyle);
        setCurrency(row.createCell(4), productCost, currencyStyle);
        setCurrency(row.createCell(5), saleExtraCosts, currencyStyle);
        setCurrency(row.createCell(6), manualExtraCosts, currencyStyle);
        setCurrency(row.createCell(7), summary.getTotalFees(), currencyStyle);
        setCurrency(row.createCell(8), summary.getTotalShipping(), currencyStyle);
        setCurrency(row.createCell(9), summary.getTotalProfit(), currencyStyle);
        setCurrency(row.createCell(10), nz(summary.getTotalProfit()).subtract(manualExtraCosts), currencyStyle);
        setCurrency(row.createCell(11), productCost.add(saleExtraCosts).add(manualExtraCosts).add(nz(summary.getTotalFees())).add(nz(summary.getTotalShipping())), currencyStyle);
        row.createCell(12).setCellValue(totalOrders);
        row.createCell(13).setCellValue(totalUnits);
        setCurrency(row.createCell(14), averageTicket, currencyStyle);

        autoSize(resumo, headers.length);
    }

    private void fillVendasSheet(Workbook workbook, List<Sale> sales, CellStyle headerStyle, CellStyle currencyStyle) {
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
        createHeaderCell(vh, 7, "Custo unitário", headerStyle);
        createHeaderCell(vh, 8, "Custo total produtos", headerStyle);
        createHeaderCell(vh, 9, "Custos extras venda", headerStyle);
        createHeaderCell(vh, 10, "Faturamento", headerStyle);
        createHeaderCell(vh, 11, "Taxas", headerStyle);
        createHeaderCell(vh, 12, "Frete", headerStyle);
        createHeaderCell(vh, 13, LUCRO_LABEL, headerStyle);

        for (Sale sale : sales) {
            Row rowVenda = vendas.createRow(vr++);

            rowVenda.createCell(0).setCellValue(sale.getSoldAt() != null ? sale.getSoldAt().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
            rowVenda.createCell(1).setCellValue(sale.getMarketplace() != null ? sale.getMarketplace().name() : "");
            rowVenda.createCell(2).setCellValue(sale.getOrderId());
            rowVenda.createCell(3).setCellValue(sale.getProductName());
            rowVenda.createCell(4).setCellValue(sale.getSku());
            rowVenda.createCell(5).setCellValue(sale.getMarketplaceItemId());
            rowVenda.createCell(6).setCellValue(sale.getQuantity());
            setCurrency(rowVenda.createCell(7), nz(sale.getProductCost()), currencyStyle);
            setCurrency(rowVenda.createCell(8), nz(sale.getProductCost()).multiply(BigDecimal.valueOf(sale.getQuantity() == null ? 0 : sale.getQuantity())), currencyStyle);
            setCurrency(rowVenda.createCell(9), nz(sale.getExtraCosts()), currencyStyle);
            setCurrency(rowVenda.createCell(10), nz(sale.getNetAmount()), currencyStyle);
            setCurrency(rowVenda.createCell(11), nz(sale.getMarketplaceFee()), currencyStyle);
            setCurrency(rowVenda.createCell(12), nz(sale.getShippingCost()), currencyStyle);
            setCurrency(rowVenda.createCell(13), nz(sale.getProfit()), currencyStyle);
        }

        autoSize(vendas, 14);
    }

    private void fillProdutosSheet(Workbook workbook, MonthlySummaryResponse summary, List<Sale> sales, CellStyle headerStyle, CellStyle currencyStyle) {
        Sheet produtos = workbook.createSheet("Fechamento por SKU");
        int pr = 0;
        Row ph = produtos.createRow(pr++);

        createHeaderCell(ph, 0, "Produto", headerStyle);
        createHeaderCell(ph, 1, "SKU", headerStyle);
        createHeaderCell(ph, 2, "Vendidas", headerStyle);
        createHeaderCell(ph, 3, "Custo unitário médio", headerStyle);
        createHeaderCell(ph, 4, "Custo total produtos", headerStyle);
        createHeaderCell(ph, 5, "Extras das vendas", headerStyle);
        createHeaderCell(ph, 6, "Faturamento", headerStyle);
        createHeaderCell(ph, 7, LUCRO_LABEL, headerStyle);

        Map<String, BigDecimal> saleExtrasBySku = sales.stream()
                .collect(Collectors.groupingBy(s -> s.getSku() == null ? "" : s.getSku(), Collectors.reducing(BigDecimal.ZERO, s -> nz(s.getExtraCosts()), BigDecimal::add)));

        for (MonthlySummaryResponse.ProductSummary p : summary.getProducts()) {
            Row rowProduto = produtos.createRow(pr++);

            rowProduto.createCell(0).setCellValue(p.getProductName());
            rowProduto.createCell(1).setCellValue(p.getSku());
            rowProduto.createCell(2).setCellValue(p.getQuantitySold());
            BigDecimal cost = nz(p.getCost());
            rowProduto.createCell(3).setCellValue(p.getQuantitySold() > 0 ? cost.divide(BigDecimal.valueOf(p.getQuantitySold()), 2, BigDecimal.ROUND_HALF_UP).doubleValue() : 0.0);
            setCurrency(rowProduto.createCell(4), cost, currencyStyle);
            setCurrency(rowProduto.createCell(5), saleExtrasBySku.getOrDefault(p.getSku(), BigDecimal.ZERO), currencyStyle);
            setCurrency(rowProduto.createCell(6), nz(p.getRevenue()), currencyStyle);
            setCurrency(rowProduto.createCell(7), nz(p.getProfit()), currencyStyle);
        }

        autoSize(produtos, 8);
    }

    private void fillManualCostsSheet(Workbook workbook, List<ExtraCost> costs, CellStyle headerStyle, CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet("Custos extras manuais");
        Row header = sheet.createRow(0);
        createHeaderCell(header, 0, "Descrição", headerStyle);
        createHeaderCell(header, 1, "Valor", headerStyle);
        int index = 1;
        BigDecimal total = BigDecimal.ZERO;
        for (ExtraCost cost : costs) {
            Row row = sheet.createRow(index++);
            row.createCell(0).setCellValue(cost.getName() == null ? "" : cost.getName());
            setCurrency(row.createCell(1), nz(cost.getValue()), currencyStyle);
            total = total.add(nz(cost.getValue()));
        }
        Row totalRow = sheet.createRow(index);
        createHeaderCell(totalRow, 0, "Total custos extras manuais", headerStyle);
        setCurrency(totalRow.createCell(1), total, currencyStyle);
        autoSize(sheet, 2);
    }

    private CellStyle createHeaderStyle(Workbook workbook) {
        Font font = workbook.createFont();
        font.setBold(true);
        CellStyle style = workbook.createCellStyle();
        style.setFont(font);
        return style;
    }

    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setDataFormat(workbook.createDataFormat().getFormat("R$ #,##0.00"));
        return style;
    }

    private void setCurrency(Cell cell, BigDecimal value, CellStyle style) {
        cell.setCellValue(nz(value).doubleValue());
        cell.setCellStyle(style);
    }

    private BigDecimal nz(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
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
