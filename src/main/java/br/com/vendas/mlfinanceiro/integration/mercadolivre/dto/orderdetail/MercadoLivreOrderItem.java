package br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail;

import java.math.BigDecimal;

public class MercadoLivreOrderItem {

    private MercadoLivreItem item;
    private Integer quantity;
    private BigDecimal unit_price; // ADICIONADO: Preço cobrado por unidade do produto
    private BigDecimal sale_fee;   // ADICIONADO: Tarifa de venda / Comissão da plataforma

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

    public BigDecimal getUnit_price() {
        return unit_price;
    }

    public void setUnit_price(BigDecimal unit_price) {
        this.unit_price = unit_price;
    }

    public BigDecimal getSale_fee() {
        return sale_fee;
    }

    public void setSale_fee(BigDecimal sale_fee) {
        this.sale_fee = sale_fee;
    }
}