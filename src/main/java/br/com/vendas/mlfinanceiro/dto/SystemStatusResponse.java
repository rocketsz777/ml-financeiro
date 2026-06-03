package br.com.vendas.mlfinanceiro.dto;

public class SystemStatusResponse {

    private String application;

    private String database;

    private String mercadoLivre;

    private String shopee;

    public String getApplication() {
        return application;
    }

    public void setApplication(
            String application
    ) {
        this.application = application;
    }

    public String getDatabase() {
        return database;
    }

    public void setDatabase(
            String database
    ) {
        this.database = database;
    }

    public String getMercadoLivre() {
        return mercadoLivre;
    }

    public void setMercadoLivre(
            String mercadoLivre
    ) {
        this.mercadoLivre = mercadoLivre;
    }

    public String getShopee() {
        return shopee;
    }

    public void setShopee(
            String shopee
    ) {
        this.shopee = shopee;
    }
}