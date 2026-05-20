package br.com.vendas.mlfinanceiro.dto;

import java.math.BigDecimal;
import java.util.List;

public class MonthlySummaryResponse {
    private String month;
    private BigDecimal totalRevenue;
    private BigDecimal totalCost;
    private BigDecimal totalFees;
    private BigDecimal totalShipping;
    private BigDecimal totalProfit;
    private List<ProductSummary> products;

    public String getMonth() { return month; }
    public void setMonth(String month) { this.month = month; }
    public BigDecimal getTotalRevenue() { return totalRevenue; }
    public void setTotalRevenue(BigDecimal totalRevenue) { this.totalRevenue = totalRevenue; }
    public BigDecimal getTotalCost() { return totalCost; }
    public void setTotalCost(BigDecimal totalCost) { this.totalCost = totalCost; }
    public BigDecimal getTotalFees() { return totalFees; }
    public void setTotalFees(BigDecimal totalFees) { this.totalFees = totalFees; }
    public BigDecimal getTotalShipping() { return totalShipping; }
    public void setTotalShipping(BigDecimal totalShipping) { this.totalShipping = totalShipping; }
    public BigDecimal getTotalProfit() { return totalProfit; }
    public void setTotalProfit(BigDecimal totalProfit) { this.totalProfit = totalProfit; }
    public List<ProductSummary> getProducts() { return products; }
    public void setProducts(List<ProductSummary> products) { this.products = products; }

    public static class ProductSummary {
        private String sku;
        private String productName;
        private int quantitySold;
        private BigDecimal revenue;
        private BigDecimal cost;
        private BigDecimal profit;

        public String getSku() { return sku; }
        public void setSku(String sku) { this.sku = sku; }
        public String getProductName() { return productName; }
        public void setProductName(String productName) { this.productName = productName; }
        public int getQuantitySold() { return quantitySold; }
        public void setQuantitySold(int quantitySold) { this.quantitySold = quantitySold; }
        public BigDecimal getRevenue() { return revenue; }
        public void setRevenue(BigDecimal revenue) { this.revenue = revenue; }
        public BigDecimal getCost() { return cost; }
        public void setCost(BigDecimal cost) { this.cost = cost; }
        public BigDecimal getProfit() { return profit; }
        public void setProfit(BigDecimal profit) { this.profit = profit; }
    }
}
