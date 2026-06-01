package br.com.vendas.mlfinanceiro.domain;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "extra_costs")
public class ExtraCost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal value;

    @Column(nullable = false)
    private Boolean active = true;

    public Long getId() {
        return id;
    }

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