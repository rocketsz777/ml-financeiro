package br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;

public class MercadoLivreShipmentResponse {

    private ShipmentCosts costs;

    @JsonProperty("base_cost")
    private BigDecimal baseCost;

    @JsonProperty("shipping_option")
    private ShippingOption shippingOption;

    public ShipmentCosts getCosts() {
        return costs;
    }

    public void setCosts(ShipmentCosts costs) {
        this.costs = costs;
    }

    public BigDecimal getBaseCost() {
        return baseCost;
    }

    public void setBaseCost(BigDecimal baseCost) {
        this.baseCost = baseCost;
    }

    public ShippingOption getShippingOption() {
        return shippingOption;
    }

    public void setShippingOption(ShippingOption shippingOption) {
        this.shippingOption = shippingOption;
    }

    public static class ShipmentCosts {

        @JsonProperty("sender_cost")
        private BigDecimal senderCost;

        public BigDecimal getSenderCost() {
            return senderCost;
        }

        public void setSenderCost(BigDecimal senderCost) {
            this.senderCost = senderCost;
        }
    }

    public static class ShippingOption {

        @JsonProperty("list_cost")
        private BigDecimal listCost;

        public BigDecimal getListCost() {
            return listCost;
        }

        public void setListCost(BigDecimal listCost) {
            this.listCost = listCost;
        }
    }
}