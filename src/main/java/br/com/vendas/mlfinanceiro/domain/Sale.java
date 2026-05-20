package br.com.vendas.mlfinanceiro.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Sale {

    private String orderId;
    private String sku;
    private String productName;
    private int quantity;
    private BigDecimal unitSalePrice;
    private BigDecimal productCost;
    private BigDecimal marketplaceFee;
    private BigDecimal shippingCost;
    private BigDecimal profit;
    private LocalDateTime soldAt;

    public Sale() {
    }

    public Sale(
            String orderId,
            String sku,
            String productName,
            int quantity,
            BigDecimal unitSalePrice,
            BigDecimal productCost,
            BigDecimal marketplaceFee,
            BigDecimal shippingCost,
            BigDecimal profit,
            LocalDateTime soldAt
    ) {

        this.orderId = orderId;
        this.sku = sku;
        this.productName = productName;
        this.quantity = quantity;
        this.unitSalePrice = unitSalePrice;
        this.productCost = productCost;
        this.marketplaceFee = marketplaceFee;
        this.shippingCost = shippingCost;
        this.profit = profit;
        this.soldAt = soldAt;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitSalePrice() {
        return unitSalePrice;
    }

    public void setUnitSalePrice(BigDecimal unitSalePrice) {
        this.unitSalePrice = unitSalePrice;
    }

    public BigDecimal getProductCost() {
        return productCost;
    }

    public void setProductCost(BigDecimal productCost) {
        this.productCost = productCost;
    }

    public BigDecimal getMarketplaceFee() {
        return marketplaceFee;
    }

    public void setMarketplaceFee(BigDecimal marketplaceFee) {
        this.marketplaceFee = marketplaceFee;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(BigDecimal shippingCost) {
        this.shippingCost = shippingCost;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public LocalDateTime getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(LocalDateTime soldAt) {
        this.soldAt = soldAt;
    }

    public BigDecimal grossRevenue() {

        return unitSalePrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    public String toCsv() {

        return safe(orderId) + ";" +
                safe(sku) + ";" +
                safe(productName) + ";" +
                quantity + ";" +
                unitSalePrice + ";" +
                productCost + ";" +
                marketplaceFee + ";" +
                shippingCost + ";" +
                profit + ";" +
                soldAt;
    }

    public static Sale fromCsv(String line) {

        String[] p = line.split(";", -1);

        return new Sale(
                emptyToNull(p[0]),
                emptyToNull(p[1]),
                emptyToNull(p[2]),
                Integer.parseInt(p[3]),
                new BigDecimal(p[4]),
                new BigDecimal(p[5]),
                new BigDecimal(p[6]),
                new BigDecimal(p[7]),
                new BigDecimal(p[8]),
                LocalDateTime.parse(p[9])
        );
    }

    private static String safe(String v) {

        if (v == null) {
            return "";
        }

        return v.replace(";", ",");
    }

    private static String emptyToNull(String v) {

        if (v == null || v.trim().isEmpty()) {
            return null;
        }

        return v.replace(",", ";");
    }
}