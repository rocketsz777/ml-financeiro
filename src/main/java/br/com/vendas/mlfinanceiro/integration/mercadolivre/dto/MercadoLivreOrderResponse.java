package br.com.vendas.mlfinanceiro.integration.mercadolivre.dto;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.Paging;

import java.util.List;

public class MercadoLivreOrderResponse {

    private List<MercadoLivreOrderResult> results;

    private Paging paging;

    public List<MercadoLivreOrderResult> getResults() {
        return results;
    }

    public void setResults(
            List<MercadoLivreOrderResult> results
    ) {
        this.results = results;
    }

    public Paging getPaging() {
        return paging;
    }

    public void setPaging(Paging paging) {
        this.paging = paging;
    }
}