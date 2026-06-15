package br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.List;

public class MercadoLivreOrderDetail {

    private Long id;
    private String status;

    @JsonProperty("date_created")
    private String dateCreated;

    @JsonProperty("date_closed")
    private String dateClosed;

    @JsonProperty("total_amount")
    private BigDecimal totalAmount;

    @JsonProperty("order_items")
    private List<MercadoLivreOrderItem> orderItems;

    private List<MercadoLivrePayment> payments;

    private Shipping shipping;

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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<MercadoLivreOrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<MercadoLivreOrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public List<MercadoLivrePayment> getPayments() {
        return payments;
    }

    public void setPayments(List<MercadoLivrePayment> payments) {
        this.payments = payments;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(
            String dateCreated
    ) {
        this.dateCreated = dateCreated;
    }

    public String getDateClosed() {
        return dateClosed;
    }

    public void setDateClosed(
            String dateClosed
    ) {
        this.dateClosed = dateClosed;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public static class Shipping {
        private Long id;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }
    }
}