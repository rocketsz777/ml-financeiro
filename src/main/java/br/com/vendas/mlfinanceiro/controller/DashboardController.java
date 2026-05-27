package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.PeriodFilter;
import br.com.vendas.mlfinanceiro.dto.DashboardResponse;
import br.com.vendas.mlfinanceiro.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

        return dashboardService
                .getSummary(
                        marketplace,
                        period
                );
    }
}