package br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail;

public class MercadoLivreOrderItem {

    private MercadoLivreItem item;

    private Integer quantity;

    public MercadoLivreItem getItem() {

        return item;
    }

    public void setItem(
            MercadoLivreItem item
    ) {

        this.item = item;
    }

    public Integer getQuantity() {

        return quantity;
    }

    public void setQuantity(
            Integer quantity
    ) {

        this.quantity = quantity;
    }
}