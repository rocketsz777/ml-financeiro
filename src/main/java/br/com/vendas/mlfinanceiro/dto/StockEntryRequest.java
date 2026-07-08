package br.com.vendas.mlfinanceiro.dto;


//DTO (Data Transfer Object) utilizado para receber os dados de entrada de estoque.
public class StockEntryRequest {

    private String sku;

    private int quantity;

    private String reference;

    //Construtores padrão vazios
    public StockEntryRequest() {
        // Intencionalmente vazio: necessário para o funcionamento da desserialização do Jackson Framework.
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}