package br.com.vendas.mlfinanceiro.dto;

import java.math.BigDecimal;

public class ProductPerformanceResponse {

    private String sku;

    private Integer soldQuantity;

    private BigDecimal revenue;

    private BigDecimal profit;

    public ProductPerformanceResponse(
            String sku,
            Integer soldQuantity,
            BigDecimal revenue,
            BigDecimal profit
    ) {
        this.sku = sku;
        this.soldQuantity = soldQuantity;
        this.revenue = revenue;
        this.profit = profit;
    }

    public String getSku() {
        return sku;
    }

    public Integer getSoldQuantity() {
        return soldQuantity;
    }

    public BigDecimal getRevenue() {
        return revenue;
    }

    public BigDecimal getProfit() {
        return profit;
    }
}