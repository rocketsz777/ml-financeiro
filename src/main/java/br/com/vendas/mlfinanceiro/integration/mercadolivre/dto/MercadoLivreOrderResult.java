package br.com.vendas.mlfinanceiro.integration.mercadolivre.dto;

import java.math.BigDecimal;

public class MercadoLivreOrderResult {

    private Long id;

    private String status;

    private BigDecimal total_amount;

    private String date_created;

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {

        this.status = status;
    }

    public BigDecimal getTotal_amount() {

        return total_amount;
    }

    public void setTotal_amount(
            BigDecimal total_amount
    ) {

        this.total_amount = total_amount;
    }

    public String getDate_created() {

        return date_created;
    }

    public void setDate_created(
            String date_created
    ) {

        this.date_created = date_created;
    }
}