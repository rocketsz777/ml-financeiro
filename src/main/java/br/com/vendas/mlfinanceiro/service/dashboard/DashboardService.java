package br.com.vendas.mlfinanceiro.service.dashboard;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.PeriodFilter;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.dto.DashboardResponse;
import br.com.vendas.mlfinanceiro.service.file.FileStoreService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final FileStoreService fileStoreService;

    public DashboardService(
            FileStoreService fileStoreService
    ) {

        this.fileStoreService =
                fileStoreService;
    }

    public DashboardResponse getSummary(
            Marketplace marketplace,
            PeriodFilter period
    ) {

        List<Sale> sales =
                fileStoreService.loadSales();

        LocalDateTime now =
                LocalDateTime.now();

        LocalDateTime limitDate =

                period == PeriodFilter.WEEK

                        ?

                        now.minusDays(7)

                        :

                        now.minusDays(30);

        List<Sale> filteredSales =

                sales.stream()

                        .filter(
                                sale -> sale.getSoldAt() != null
                        )

                        .filter(
                                sale ->
                                        sale.getSoldAt()
                                                .isAfter(limitDate)
                        )

                        .filter(
                                sale -> marketplace == null
                                        || sale.getMarketplace() == marketplace
                        )

                        .collect(
                                Collectors.toList()
                        );

        BigDecimal totalRevenue =

                filteredSales.stream()

                        .map(
                                Sale::getGrossAmount
                        )

                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalCost =

                filteredSales.stream()

                        .map(
                                sale ->

                                        sale.getProductCost() != null

                                                ?

                                                sale.getProductCost()
                                                        .multiply(
                                                                BigDecimal.valueOf(
                                                                        sale.getQuantity()
                                                                )
                                                        )

                                                :

                                                BigDecimal.ZERO
                        )

                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        BigDecimal totalProfit =

                filteredSales.stream()

                        .map(
                                sale -> sale.getProfit() != null
                                        ? sale.getProfit()
                                        : BigDecimal.ZERO
                        )

                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        int unitsSold =

                filteredSales.stream()

                        .mapToInt(
                                Sale::getQuantity
                        )

                        .sum();

        BigDecimal profitMargin =
                BigDecimal.ZERO;

        if (
                totalCost.compareTo(
                        BigDecimal.ZERO
                ) > 0
        ) {

            profitMargin =

                    totalProfit
                            .multiply(
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

        Map<String, Integer> topSellingItems =
                buildTopSellingItems(
                        filteredSales
                );

        Map<String, BigDecimal> topProfitableItems =
                buildTopProfitableItems(
                        filteredSales
                );

        DashboardResponse response =
                new DashboardResponse();

        response.setMarketplace(
                marketplace == null
                        ? "ALL"
                        : marketplace.name()
        );

        response.setTotalRevenue(
                totalRevenue
        );

        response.setTotalCost(
                totalCost
        );

        response.setTotalProfit(
                totalProfit
        );

        response.setUnitsSold(
                unitsSold
        );

        response.setProfitMargin(
                profitMargin
        );

        response.setTopSellingItems(
                topSellingItems
        );

        response.setTopProfitableItems(
                topProfitableItems
        );

        return response;
    }

    private Map<String, Integer> buildTopSellingItems(
            List<Sale> filteredSales
    ) {

        return filteredSales.stream()

                .collect(
                        Collectors.groupingBy(

                                sale ->

                                        sale.getProductName() != null
                                                && !sale.getProductName()
                                                .trim()
                                                .isEmpty()

                                                ?

                                                sale.getProductName()

                                                :

                                                sale.getSku(),

                                Collectors.summingInt(
                                        Sale::getQuantity
                                )
                        )
                )

                .entrySet()

                .stream()

                .sorted(
                        Map.Entry.<String, Integer>
                                        comparingByValue()

                                .reversed()
                )

                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a, b) -> a,
                                java.util.LinkedHashMap::new
                        )
                );
    }

    private Map<String, BigDecimal> buildTopProfitableItems(
            List<Sale> filteredSales
    ) {

        return filteredSales.stream()

                .collect(
                        Collectors.groupingBy(

                                sale ->

                                        sale.getProductName() != null
                                                && !sale.getProductName()
                                                .trim()
                                                .isEmpty()

                                                ?

                                                sale.getProductName()

                                                :

                                                sale.getSku(),

                                Collectors.reducing(
                                        BigDecimal.ZERO,

                                        sale -> sale.getProfit() != null
                                                ? sale.getProfit()
                                                : BigDecimal.ZERO,

                                        BigDecimal::add
                                )
                        )
                )

                .entrySet()

                .stream()

                .sorted(
                        Map.Entry.<String, BigDecimal>
                                        comparingByValue()

                                .reversed()
                )

                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (a, b) -> a,
                                java.util.LinkedHashMap::new
                        )
                );
    }
}