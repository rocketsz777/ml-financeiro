package br.com.vendas.mlfinanceiro.dto;

import java.math.BigDecimal;
import java.util.Map;

public class DashboardResponse {

    private String marketplace;

    private BigDecimal monthlyRevenue;

    private BigDecimal weeklyRevenue;

    private BigDecimal totalRevenue;

    private BigDecimal totalCost;

    private BigDecimal totalExtraCosts;

    private BigDecimal totalProfit;

    private Integer unitsSold;

    private BigDecimal profitMargin;

    private Integer salesCount;

    private Map<String, Integer> topSellingItems;

    private Map<String, BigDecimal> topProfitableItems;

    private Map<String, BigDecimal> productCosts;

    private Map<String, BigDecimal> productCostBreakdown;

    public String getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(
            String marketplace
    ) {
        this.marketplace = marketplace;
    }

    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(
            BigDecimal monthlyRevenue
    ) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public BigDecimal getWeeklyRevenue() {
        return weeklyRevenue;
    }

    public void setWeeklyRevenue(
            BigDecimal weeklyRevenue
    ) {
        this.weeklyRevenue = weeklyRevenue;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }

    public void setTotalRevenue(
            BigDecimal totalRevenue
    ) {
        this.totalRevenue = totalRevenue;
    }

    public BigDecimal getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(
            BigDecimal totalCost
    ) {
        this.totalCost = totalCost;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(
            BigDecimal totalProfit
    ) {
        this.totalProfit = totalProfit;
    }

    public Integer getUnitsSold() {
        return unitsSold;
    }

    public void setUnitsSold(
            Integer unitsSold
    ) {
        this.unitsSold = unitsSold;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(
            BigDecimal profitMargin
    ) {
        this.profitMargin = profitMargin;
    }

    public Map<String, Integer> getTopSellingItems() {
        return topSellingItems;
    }

    public void setTopSellingItems(
            Map<String, Integer> topSellingItems
    ) {
        this.topSellingItems = topSellingItems;
    }

    public Map<String, BigDecimal> getTopProfitableItems() {
        return topProfitableItems;
    }

    public void setTopProfitableItems(
            Map<String, BigDecimal> topProfitableItems
    ) {
        this.topProfitableItems = topProfitableItems;
    }

    public Map<String, BigDecimal> getProductCosts() {
        return productCosts;
    }

    public void setProductCosts(
            Map<String, BigDecimal> productCosts
    ) {
        this.productCosts = productCosts;
    }

    public Map<String, BigDecimal> getProductCostBreakdown() {
        return productCostBreakdown;
    }

    public void setProductCostBreakdown(
            Map<String, BigDecimal> productCostBreakdown) {
        this.productCostBreakdown = productCostBreakdown;
    }

    public BigDecimal getTotalExtraCosts() {
        return totalExtraCosts;
    }

    public void setTotalExtraCosts(
            BigDecimal totalExtraCosts
    ) {
        this.totalExtraCosts = totalExtraCosts;
    }

    public Integer getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(Integer salesCount) {
        this.salesCount = salesCount;
    }
}