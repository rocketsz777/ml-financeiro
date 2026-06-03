package br.com.vendas.mlfinanceiro.dto;

public class ImportSalesResponse {

    private Integer mercadoLivreImported;

    private Integer shopeeImported;

    private Integer totalImported;

    public Integer getMercadoLivreImported() {
        return mercadoLivreImported;
    }

    public void setMercadoLivreImported(
            Integer mercadoLivreImported
    ) {
        this.mercadoLivreImported = mercadoLivreImported;
    }

    public Integer getShopeeImported() {
        return shopeeImported;
    }

    public void setShopeeImported(
            Integer shopeeImported
    ) {
        this.shopeeImported = shopeeImported;
    }

    public Integer getTotalImported() {
        return totalImported;
    }

    public void setTotalImported(
            Integer totalImported
    ) {
        this.totalImported = totalImported;
    }
}