package br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail;

import java.math.BigDecimal;
import java.util.List;

public class MercadoLivreOrderDetail {

    private Long id;
    private String status;
    private BigDecimal total_amount;
    private List<MercadoLivreOrderItem> order_items;
    private List<MercadoLivrePayment> payments;
    private Shipping shipping; // ADICIONADO: Objeto de ligação logística do pedido

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

    public void setTotal_amount(BigDecimal total_amount) {
        this.total_amount = total_amount;
    }

    public List<MercadoLivreOrderItem> getOrder_items() {
        return order_items;
    }

    public void setOrder_items(List<MercadoLivreOrderItem> order_items) {
        this.order_items = order_items;
    }

    public List<MercadoLivrePayment> getPayments() {
        return payments;
    }

    public void setPayments(List<MercadoLivrePayment> payments) {
        this.payments = payments;
    }

    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    // ADICIONADO: Classe estática aninhada para expor o ID do frete (Shipment ID)
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