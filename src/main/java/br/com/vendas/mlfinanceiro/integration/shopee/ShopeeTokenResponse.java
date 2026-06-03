package br.com.vendas.mlfinanceiro.integration.shopee;

public class ShopeeTokenResponse {

    private String access_token;

    private String refresh_token;

    private Long expire_in;

    private Long shop_id;

    public String getAccess_token() {
        return access_token;
    }

    public void setAccess_token(
            String access_token
    ) {
        this.access_token = access_token;
    }

    public String getRefresh_token() {
        return refresh_token;
    }

    public void setRefresh_token(
            String refresh_token
    ) {
        this.refresh_token = refresh_token;
    }

    public Long getExpire_in() {
        return expire_in;
    }

    public void setExpire_in(
            Long expire_in
    ) {
        this.expire_in = expire_in;
    }

    public Long getShop_id() {
        return shop_id;
    }

    public void setShop_id(
            Long shop_id
    ) {
        this.shop_id = shop_id;
    }
}