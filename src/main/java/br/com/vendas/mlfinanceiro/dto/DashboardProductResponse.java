package br.com.vendas.mlfinanceiro.dto;

import java.math.BigDecimal;

public class DashboardProductResponse {

    private String productName;

    private Integer quantitySold;

    private BigDecimal profit;

    public String getProductName() {
        return productName;
    }

    public void setProductName(
            String productName
    ) {
        this.productName = productName;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public void setQuantitySold(
            Integer quantitySold
    ) {
        this.quantitySold = quantitySold;
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