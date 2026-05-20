package br.com.vendas.mlfinanceiro.domain;

import java.math.BigDecimal;

public class Product {
    private String sku;
    private String mlItemId;
    private String name;
    private BigDecimal costPrice;
    private int stock;

    public Product() {}

    public Product(String sku, String mlItemId, String name, BigDecimal costPrice, int stock) {
        this.sku = sku;
        this.mlItemId = mlItemId;
        this.name = name;
        this.costPrice = costPrice;
        this.stock = stock;
    }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getMlItemId() { return mlItemId; }
    public void setMlItemId(String mlItemId) { this.mlItemId = mlItemId; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getCostPrice() { return costPrice; }
    public void setCostPrice(BigDecimal costPrice) { this.costPrice = costPrice; }
    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String toCsv() {
        return escape(sku) + ";" + escape(mlItemId) + ";" + escape(name) + ";" + costPrice + ";" + stock;
    }

    public static Product fromCsv(String line) {
        String[] p = line.split(";", -1);
        return new Product(unescape(p[0]), unescape(p[1]), unescape(p[2]), new BigDecimal(p[3]), Integer.parseInt(p[4]));
    }

    private static String escape(String v) {
        return v == null ? "" : v.replace(";", "\u003B");
    }

    private static String unescape(String v) {
        return v == null ? null : v.replace("\u003B", ";");
    }
}
