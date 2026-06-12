package br.com.vendas.mlfinanceiro.dto;

import java.math.BigDecimal;

public class ProductRequest {

    private String sku;

    private String mlItemId;

    private String name;

    private BigDecimal costPrice;

    private Integer stockQuantity;

    private BigDecimal oldCostPrice;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getMlItemId() {
        return mlItemId;
    }

    public void setMlItemId(String mlItemId) {
        this.mlItemId = mlItemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getCostPrice() {
        return costPrice;
    }

    public BigDecimal getOldCostPrice() {
        return oldCostPrice;
    }

    public void setOldCostPrice(
            BigDecimal oldCostPrice
    ) {
        this.oldCostPrice = oldCostPrice;
    }

    public void setCostPrice(BigDecimal costPrice) {
        this.costPrice = costPrice;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }



}