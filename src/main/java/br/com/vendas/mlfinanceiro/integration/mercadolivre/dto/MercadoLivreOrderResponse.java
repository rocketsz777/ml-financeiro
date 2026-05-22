package br.com.vendas.mlfinanceiro.integration.mercadolivre.dto;

import java.util.List;

public class MercadoLivreOrderResponse {

    private List<MercadoLivreOrderResult> results;

    public List<MercadoLivreOrderResult> getResults() {
        return results;
    }

    public void setResults(
            List<MercadoLivreOrderResult> results
    ) {

        this.results = results;
    }
}