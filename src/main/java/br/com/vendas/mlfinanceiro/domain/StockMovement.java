package br.com.vendas.mlfinanceiro.domain;

import java.time.LocalDateTime;

public class StockMovement {

    private String sku;

    private StockMovementType type;

    private int quantity;

    private String reference;

    private LocalDateTime createdAt;

    public StockMovement() {
    }

    public StockMovement(
            String sku,
            StockMovementType type,
            int quantity,
            String reference,
            LocalDateTime createdAt
    ) {

        this.sku = sku;
        this.type = type;
        this.quantity = quantity;
        this.reference = reference;
        this.createdAt = createdAt;
    }

    public String getSku() {

        return sku;
    }

    public void setSku(
            String sku
    ) {

        this.sku = sku;
    }

    public StockMovementType getType() {

        return type;
    }

    public void setType(
            StockMovementType type
    ) {

        this.type = type;
    }

    public int getQuantity() {

        return quantity;
    }

    public void setQuantity(
            int quantity
    ) {

        this.quantity = quantity;
    }

    public String getReference() {

        return reference;
    }

    public void setReference(
            String reference
    ) {

        this.reference = reference;
    }

    public LocalDateTime getCreatedAt() {

        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {

        this.createdAt = createdAt;
    }

    public String toCsv() {

        return safe(sku) + ";" +
                type + ";" +
                quantity + ";" +
                safe(reference) + ";" +
                createdAt;
    }

    public static StockMovement fromCsv(
            String line
    ) {

        String[] p =
                line.split(";", -1);

        return new StockMovement(
                p[0],
                StockMovementType.valueOf(
                        p[1]
                ),
                Integer.parseInt(
                        p[2]
                ),
                p[3],
                LocalDateTime.parse(
                        p[4]
                )
        );
    }

    private static String safe(
            String value
    ) {

        if (value == null) {

            return "";
        }

        return value.replace(
                ";",
                ","
        );
    }
}