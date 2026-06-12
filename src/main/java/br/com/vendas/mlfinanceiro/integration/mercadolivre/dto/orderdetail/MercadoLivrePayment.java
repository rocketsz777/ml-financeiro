package br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class MercadoLivrePayment {

    private Long id;

    @JsonProperty("transaction_amount")
    private BigDecimal transactionAmount;

    @JsonProperty("total_paid_amount")
    private BigDecimal totalPaidAmount;

    @JsonProperty("marketplace_fee")
    private BigDecimal marketplaceFee;

    @JsonProperty("net_received_amount")
    private BigDecimal netReceivedAmount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transactionAmount) {
        this.transactionAmount = transactionAmount;
    }

    public BigDecimal getTotal_paid_amount() {
        return totalPaidAmount;
    }

    public void setTotal_paid_amount(BigDecimal totalPaidAmount) {
        this.totalPaidAmount = totalPaidAmount;
    }

    public BigDecimal getMarketplaceFee() {
        return marketplaceFee;
    }

    public void setMarketplaceFee(BigDecimal marketplaceFee) {
        this.marketplaceFee = marketplaceFee;
    }

    public BigDecimal getNetReceivedAmount() {
        return netReceivedAmount;
    }

    public void setNetReceivedAmount(BigDecimal netReceivedAmount) {
        this.netReceivedAmount = netReceivedAmount;
    }
}