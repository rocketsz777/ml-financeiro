package br.com.vendas.mlfinanceiro.dto;

import java.math.BigDecimal;

public class DashboardMonthlyResponse {

    private String month;

    private BigDecimal revenue;

    private BigDecimal profit;

    public String getMonth() {
        return month;
    }

    public void setMonth(
            String month
    ) {
        this.month = month;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public void setRevenue(
            BigDecimal revenue
    ) {
        this.revenue = revenue;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(
            BigDecimal profit
    ) {
        this.profit = profit;
    }
}