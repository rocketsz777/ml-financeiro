package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import java.time.LocalDateTime;

public class MercadoLivreTokenResponse {

    private String access_token;

    private String token_type;

    private Integer expires_in;

    private String scope;

    private Long user_id;

    private String refresh_token;

    private LocalDateTime created_at;

    public String getAccess_token() {

        return access_token;
    }

    public void setAccess_token(
            String access_token
    ) {

        this.access_token =
                access_token;
    }

    public String getToken_type() {

        return token_type;
    }

    public void setToken_type(
            String token_type
    ) {

        this.token_type =
                token_type;
    }

    public Integer getExpires_in() {

        return expires_in;
    }

    public void setExpires_in(
            Integer expires_in
    ) {

        this.expires_in =
                expires_in;
    }

    public String getScope() {

        return scope;
    }

    public void setScope(
            String scope
    ) {

        this.scope =
                scope;
    }

    public Long getUser_id() {

        return user_id;
    }

    public void setUser_id(
            Long user_id
    ) {

        this.user_id =
                user_id;
    }

    public String getRefresh_token() {

        return refresh_token;
    }

    public void setRefresh_token(
            String refresh_token
    ) {

        this.refresh_token =
                refresh_token;
    }

    public LocalDateTime getCreated_at() {

        return created_at;
    }

    public void setCreated_at(
            LocalDateTime created_at
    ) {

        this.created_at =
                created_at;
    }
}