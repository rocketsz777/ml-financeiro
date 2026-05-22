package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import org.springframework.stereotype.Component;

@Component
public class MercadoLivreTokenStore {

    private MercadoLivreTokenResponse token;

    public void save(
            MercadoLivreTokenResponse token
    ) {

        this.token = token;
    }

    public MercadoLivreTokenResponse getToken() {

        return token;
    }

    public boolean hasToken() {

        return token != null;
    }
}