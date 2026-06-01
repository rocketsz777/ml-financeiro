package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.PeriodFilter;
import br.com.vendas.mlfinanceiro.dto.DashboardMonthlyResponse;
import br.com.vendas.mlfinanceiro.dto.DashboardProductResponse;
import br.com.vendas.mlfinanceiro.dto.DashboardResponse;
import br.com.vendas.mlfinanceiro.dto.DashboardWeeklyResponse;
import br.com.vendas.mlfinanceiro.service.dashboard.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(
            DashboardService dashboardService
    ) {

        this.dashboardService =
                dashboardService;
    }

    @GetMapping("/summary")
    public DashboardResponse summary(

            @RequestParam(
                    required = false
            )
            Marketplace marketplace,

            @RequestParam(
                    defaultValue = "MONTH"
            )
            PeriodFilter period
    ) {

        return dashboardService.getSummary(
                marketplace,
                period
        );
    }

    @GetMapping("/weekly")
    public List<DashboardWeeklyResponse> weekly() {

        return dashboardService.getWeekly();
    }

    @GetMapping("/monthly")
    public List<DashboardMonthlyResponse> monthly() {

        return dashboardService.getMonthly();
    }

    @GetMapping("/products")
    public List<DashboardProductResponse> products() {

        return dashboardService.getProducts();
    }
}