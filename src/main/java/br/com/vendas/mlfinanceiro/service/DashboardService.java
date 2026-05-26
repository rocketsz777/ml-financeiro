package br.com.vendas.mlfinanceiro.service;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class DashboardService {

    private final FileStoreService fileStoreService;

    public DashboardService(
            FileStoreService fileStoreService
    ) {

        this.fileStoreService =
                fileStoreService;
    }

    public Map<String, Object> getSummary(
            Marketplace marketplace
    ) {

        List<Sale> sales =
                fileStoreService.loadSales();

        LocalDateTime now =
                LocalDateTime.now();

        BigDecimal weeklyRevenue =
                BigDecimal.ZERO;

        BigDecimal monthlyRevenue =
                BigDecimal.ZERO;

        BigDecimal totalProfit =
                BigDecimal.ZERO;

        BigDecimal totalCost =
                BigDecimal.ZERO;

        int unitsSold = 0;

        Map<String, Integer> topSellingItems =
                new HashMap<>();

        for (Sale sale : sales) {

            if (sale.getSoldAt() == null) {

                continue;
            }

            if (marketplace != null &&
                    sale.getMarketplace() != marketplace) {

                continue;
            }

            BigDecimal gross =
                    sale.getGrossAmount() != null
                            ? sale.getGrossAmount()
                            : BigDecimal.ZERO;

            BigDecimal profit =
                    sale.getProfit() != null
                            ? sale.getProfit()
                            : BigDecimal.ZERO;

            BigDecimal productCost =
                    sale.getProductCost() != null
                            ? sale.getProductCost()
                            : BigDecimal.ZERO;

            totalProfit =
                    totalProfit.add(
                            profit
                    );

            totalCost =
                    totalCost.add(
                            productCost
                    );

            unitsSold +=
                    sale.getQuantity();

            if (sale.getSoldAt()
                    .isAfter(
                            now.minusDays(7)
                    )) {

                weeklyRevenue =
                        weeklyRevenue.add(
                                gross
                        );
            }

            if (sale.getSoldAt()
                    .isAfter(
                            now.minusDays(30)
                    )) {

                monthlyRevenue =
                        monthlyRevenue.add(
                                gross
                        );
            }

            String productKey =
                    sale.getProductName();

            if (productKey == null ||
                    productKey.trim().isEmpty()) {

                productKey =
                        sale.getSku();
            }

            topSellingItems.put(
                    productKey,
                    topSellingItems.getOrDefault(
                            productKey,
                            0
                    ) + sale.getQuantity()
            );
        }

        BigDecimal profitMargin =
                BigDecimal.ZERO;

        if (totalCost.compareTo(
                BigDecimal.ZERO
        ) > 0) {

            profitMargin =
                    totalProfit.multiply(
                                    BigDecimal.valueOf(
                                            100
                                    )
                            )
                            .divide(
                                    totalCost,
                                    2,
                                    RoundingMode.HALF_UP
                            );
        }

        Map<String, Object> summary =
                new HashMap<>();

        summary.put(
                "marketplace",
                marketplace == null
                        ? "ALL"
                        : marketplace
        );

        summary.put(
                "weeklyRevenue",
                weeklyRevenue
        );

        summary.put(
                "monthlyRevenue",
                monthlyRevenue
        );

        summary.put(
                "totalProfit",
                totalProfit
        );

        summary.put(
                "unitsSold",
                unitsSold
        );

        summary.put(
                "topSellingItems",
                topSellingItems
        );

        summary.put(
                "profitMargin",
                profitMargin
        );

        return summary;
    }
}