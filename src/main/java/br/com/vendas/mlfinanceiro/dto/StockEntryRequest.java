package br.com.vendas.mlfinanceiro.dto;

public class StockEntryRequest {

    private String sku;

    private int quantity;

    private String reference;

    public StockEntryRequest() {
    }

    public String getSku() {

        return sku;
    }

    public void setSku(
            String sku
    ) {

        this.sku = sku;
    }

    public int getQuantity() {

        return quantity;
    }

    public void setQuantity(
            int quantity
    ) {

        this.quantity = quantity;
    }

    public String getReference() {

        return reference;
    }

    public void setReference(
            String reference
    ) {

        this.reference = reference;
    }
}