package br.com.vendas.mlfinanceiro.dto;

import java.math.BigDecimal;

public class SaleRequest {
    private String orderId;
    private String sku;
    private String productName;
    private int quantity;
    private BigDecimal unitSalePrice;
    private BigDecimal marketplaceFee = BigDecimal.ZERO;
    private BigDecimal shippingCost = BigDecimal.ZERO;

    public String getOrderId() { return orderId; }
    public void setOrderId(String orderId) { this.orderId = orderId; }
    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }
    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public BigDecimal getUnitSalePrice() { return unitSalePrice; }
    public void setUnitSalePrice(BigDecimal unitSalePrice) { this.unitSalePrice = unitSalePrice; }
    public BigDecimal getMarketplaceFee() { return marketplaceFee; }
    public void setMarketplaceFee(BigDecimal marketplaceFee) { this.marketplaceFee = marketplaceFee; }
    public BigDecimal getShippingCost() { return shippingCost; }
    public void setShippingCost(BigDecimal shippingCost) { this.shippingCost = shippingCost; }
}
