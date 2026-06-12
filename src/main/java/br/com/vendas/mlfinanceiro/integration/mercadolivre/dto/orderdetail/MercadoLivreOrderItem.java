package br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

public class MercadoLivreOrderItem {

    private MercadoLivreItem item;
    private Integer quantity;

    @JsonProperty("unit_price")
    private BigDecimal unitPrice;

    @JsonProperty("sale_fee")
    private BigDecimal saleFee;

    public MercadoLivreItem getItem() {
        return item;
    }

    public void setItem(MercadoLivreItem item) {
        this.item = item;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public BigDecimal getSaleFee() {
        return saleFee;
    }

    public void setSaleFee(BigDecimal saleFee) {
        this.saleFee = saleFee;
    }

}