package br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail;

public class MercadoLivreItem {

    private String id;

    private String title;

    private String seller_sku;

    public String getId() {

        return id;
    }

    public void setId(String id) {

        this.id = id;
    }

    public String getTitle() {

        return title;
    }

    public void setTitle(String title) {

        this.title = title;
    }

    public String getSeller_sku() {

        return seller_sku;
    }

    public void setSeller_sku(
            String seller_sku
    ) {

        this.seller_sku = seller_sku;
    }

    public String getSellerSku() {

        return seller_sku;
    }

    public void setSellerSku(
            String sellerSku
    ) {

        this.seller_sku = sellerSku;
    }
}