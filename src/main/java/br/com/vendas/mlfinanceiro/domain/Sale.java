package br.com.vendas.mlfinanceiro.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Sale {

    private String orderId;

    private String sku;

    private String productName;

    private Marketplace marketplace;

    private int quantity;

    private BigDecimal grossAmount;

    private BigDecimal netAmount;

    private BigDecimal unitSalePrice;

    private BigDecimal productCost;

    private BigDecimal extraCosts;

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
            Marketplace marketplace,
            int quantity,
            BigDecimal grossAmount,
            BigDecimal netAmount,
            BigDecimal unitSalePrice,
            BigDecimal productCost,
            BigDecimal extraCosts,
            BigDecimal marketplaceFee,
            BigDecimal shippingCost,
            BigDecimal profit,
            LocalDateTime soldAt
    ) {

        this.orderId = orderId;
        this.sku = sku;
        this.productName = productName;
        this.marketplace = marketplace;
        this.quantity = quantity;
        this.grossAmount = grossAmount;
        this.netAmount = netAmount;
        this.unitSalePrice = unitSalePrice;
        this.productCost = productCost;
        this.extraCosts = extraCosts;
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

    public Marketplace getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(Marketplace marketplace) {
        this.marketplace = marketplace;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(BigDecimal grossAmount) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(BigDecimal netAmount) {
        this.netAmount = netAmount;
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

    public BigDecimal getExtraCosts() {
        return extraCosts;
    }

    public void setExtraCosts(BigDecimal extraCosts) {
        this.extraCosts = extraCosts;
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

        if (grossAmount != null) {

            return grossAmount;
        }

        return unitSalePrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    public String toCsv() {

        return safe(orderId) + ";" +
                safe(sku) + ";" +
                safe(productName) + ";" +
                marketplace + ";" +
                quantity + ";" +
                grossAmount + ";" +
                netAmount + ";" +
                unitSalePrice + ";" +
                productCost + ";" +
                extraCosts + ";" +
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
                Marketplace.valueOf(p[3]),
                Integer.parseInt(p[4]),
                parseBigDecimal(p[5]),
                parseBigDecimal(p[6]),
                parseBigDecimal(p[7]),
                parseBigDecimal(p[8]),
                parseBigDecimal(p[9]),
                parseBigDecimal(p[10]),
                parseBigDecimal(p[11]),
                parseBigDecimal(p[12]),
                LocalDateTime.parse(p[13])
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

    private static BigDecimal parseBigDecimal(String value) {

        if (value == null ||
                value.trim().isEmpty() ||
                value.equals("null")) {

            return BigDecimal.ZERO;
        }

        return new BigDecimal(value);
    }
}