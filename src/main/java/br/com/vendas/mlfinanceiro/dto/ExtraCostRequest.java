package br.com.vendas.mlfinanceiro.dto;

import java.math.BigDecimal;

public class ExtraCostRequest {

    private String name;

    private BigDecimal value;

    private Boolean active;

    public String getName() {
        return name;
    }

    public void setName(
            String name
    ) {
        this.name = name;
    }

    public BigDecimal getValue() {
        return value;
    }

    public void setValue(
            BigDecimal value
    ) {
        this.value = value;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(
            Boolean active
    ) {
        this.active = active;
    }
}