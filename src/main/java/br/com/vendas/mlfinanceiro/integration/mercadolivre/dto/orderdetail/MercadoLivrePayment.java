package br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail;

import java.math.BigDecimal;

public class MercadoLivrePayment {

    private Long id;

    private BigDecimal transaction_amount;

    private BigDecimal total_paid_amount;

    private BigDecimal marketplace_fee;

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public BigDecimal getTransaction_amount() {

        return transaction_amount;
    }

    public void setTransaction_amount(
            BigDecimal transaction_amount
    ) {

        this.transaction_amount =
                transaction_amount;
    }

    public BigDecimal getTotal_paid_amount() {

        return total_paid_amount;
    }

    public void setTotal_paid_amount(
            BigDecimal total_paid_amount
    ) {

        this.total_paid_amount =
                total_paid_amount;
    }

    public BigDecimal getMarketplace_fee() {

        return marketplace_fee;
    }

    public void setMarketplace_fee(
            BigDecimal marketplace_fee
    ) {

        this.marketplace_fee =
                marketplace_fee;
    }
}