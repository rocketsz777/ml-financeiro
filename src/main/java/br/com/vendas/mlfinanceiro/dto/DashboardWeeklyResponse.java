package br.com.vendas.mlfinanceiro.dto;

import java.math.BigDecimal;

public class DashboardWeeklyResponse {

    private String date;

    private BigDecimal revenue;

    private BigDecimal profit;

    public String getDate() {
        return date;
    }

    public void setDate(
            String date
    ) {
        this.date = date;
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