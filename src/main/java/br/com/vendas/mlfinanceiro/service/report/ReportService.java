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

    private final FileStoreService fileStoreService;

    private final SaleRepository saleRepository;

    public ReportService(
            FileStoreService fileStoreService,
            SaleRepository saleRepository
    ) {

        this.fileStoreService =
                fileStoreService;

        this.saleRepository =
                saleRepository;
    }

    public MonthlySummaryResponse monthlySummary(
            YearMonth month
    ) {

        List<Sale> sales =
                saleRepository.findAll()
                        .stream()
                        .filter(
                                s -> YearMonth.from(
                                        s.getSoldAt()
                                ).equals(month)
                        )
                        .collect(Collectors.toList());

        BigDecimal totalRevenue =
                sales.stream()
                        .map(Sale::getGrossAmount)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalCost =
                sales.stream()
                        .map(Sale::getProductCost)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalFees =
                sales.stream()
                        .map(Sale::getMarketplaceFee)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalShipping =
                sales.stream()
                        .map(Sale::getShippingCost)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalProfit =
                sales.stream()
                        .map(Sale::getProfit)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        Map<String, List<Sale>> grouped =
                sales.stream()
                        .collect(
                                Collectors.groupingBy(
                                        Sale::getSku
                                )
                        );

        List<MonthlySummaryResponse.ProductSummary> products =
                new ArrayList<MonthlySummaryResponse.ProductSummary>();

        for (Map.Entry<String, List<Sale>> e : grouped.entrySet()) {

            List<Sale> list =
                    e.getValue();

            MonthlySummaryResponse.ProductSummary item =
                    new MonthlySummaryResponse.ProductSummary();

            item.setSku(
                    e.getKey()
            );

            item.setProductName(
                    list.get(0)
                            .getProductName()
            );

            item.setQuantitySold(
                    list.stream()
                            .mapToInt(
                                    Sale::getQuantity
                            )
                            .sum()
            );

            item.setRevenue(
                    list.stream()
                            .map(
                                    Sale::getGrossAmount
                            )
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            )
            );

            item.setCost(
                    list.stream()
                            .map(
                                    Sale::getProductCost
                            )
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            )
            );

            item.setProfit(
                    list.stream()
                            .map(
                                    Sale::getProfit
                            )
                            .reduce(
                                    BigDecimal.ZERO,
                                    BigDecimal::add
                            )
            );

            products.add(
                    item
            );
        }

        MonthlySummaryResponse response =
                new MonthlySummaryResponse();

        response.setMonth(
                month.toString()
        );

        response.setTotalRevenue(
                totalRevenue
        );

        response.setTotalCost(
                totalCost
        );

        response.setTotalFees(
                totalFees
        );

        response.setTotalShipping(
                totalShipping
        );

        response.setTotalProfit(
                totalProfit
        );

        response.setProducts(
                products
        );

        return response;
    }

    public Path generateExcel(
            YearMonth month
    ) {

        MonthlySummaryResponse summary =
                monthlySummary(
                        month
                );

        Path output =
                fileStoreService.getDataDir()
                        .resolve(
                                "fechamento-" +
                                        month +
                                        ".xlsx"
                        );

        try (Workbook workbook =
                     new XSSFWorkbook()) {

            Sheet resumo =
                    workbook.createSheet(
                            "Resumo"
                    );

            Sheet vendas =
                    workbook.createSheet(
                            "Vendas"
                    );

            Sheet produtos =
                    workbook.createSheet(
                            "Produtos"
                    );

            CellStyle headerStyle =
                    createHeaderStyle(
                            workbook
                    );

            int r = 0;

            Row h =
                    resumo.createRow(
                            r++
                    );

            createHeaderCell(
                    h,
                    0,
                    "Mês",
                    headerStyle
            );

            createHeaderCell(
                    h,
                    1,
                    "Faturamento",
                    headerStyle
            );

            createHeaderCell(
                    h,
                    2,
                    "Custos",
                    headerStyle
            );

            createHeaderCell(
                    h,
                    3,
                    "Taxas",
                    headerStyle
            );

            createHeaderCell(
                    h,
                    4,
                    "Frete",
                    headerStyle
            );

            createHeaderCell(
                    h,
                    5,
                    "Lucro",
                    headerStyle
            );

            Row row =
                    resumo.createRow(
                            r++
                    );

            row.createCell(0)
                    .setCellValue(
                            summary.getMonth()
                    );

            row.createCell(1)
                    .setCellValue(
                            summary.getTotalRevenue()
                                    .doubleValue()
                    );

            row.createCell(2)
                    .setCellValue(
                            summary.getTotalCost()
                                    .doubleValue()
                    );

            row.createCell(3)
                    .setCellValue(
                            summary.getTotalFees()
                                    .doubleValue()
                    );

            row.createCell(4)
                    .setCellValue(
                            summary.getTotalShipping()
                                    .doubleValue()
                    );

            row.createCell(5)
                    .setCellValue(
                            summary.getTotalProfit()
                                    .doubleValue()
                    );

            autoSize(
                    resumo,
                    6
            );

            try (OutputStream os =
                         Files.newOutputStream(
                                 output
                         )) {

                workbook.write(
                        os
                );
            }

            return output;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao gerar Excel",
                    e
            );
        }
    }

    private CellStyle createHeaderStyle(
            Workbook workbook
    ) {

        Font font =
                workbook.createFont();

        font.setBold(
                true
        );

        CellStyle style =
                workbook.createCellStyle();

        style.setFont(
                font
        );

        return style;
    }

    private void createHeaderCell(
            Row row,
            int col,
            String value,
            CellStyle style
    ) {

        Cell cell =
                row.createCell(
                        col
                );

        cell.setCellValue(
                value
        );

        cell.setCellStyle(
                style
        );
    }

    private void autoSize(
            Sheet sheet,
            int columns
    ) {

        for (int i = 0; i < columns; i++) {

            sheet.autoSizeColumn(
                    i
            );
        }
    }
}