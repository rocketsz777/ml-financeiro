package br.com.vendas.mlfinanceiro.domain;

import javax.persistence.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Entity
@Table(name = "sales")
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String orderId;

    private String sku;

    private String productName;

    @Enumerated(EnumType.STRING)
    private Marketplace marketplace;

    private Integer quantity;

    private BigDecimal grossAmount;

    private BigDecimal netAmount;

    private BigDecimal unitSalePrice;

    private BigDecimal productCost;

    private BigDecimal marketplaceFee;

    private BigDecimal shippingCost;

    private BigDecimal extraCosts;

    private BigDecimal profit;

    private BigDecimal profitMargin;

    private LocalDateTime soldAt;

    private String marketplaceItemId;

    public void calculateProfit() {

        BigDecimal totalCost =

                productCost
                        .multiply(
                                BigDecimal.valueOf(
                                        quantity
                                )
                        )
                        .add(
                                extraCosts
                        );

        this.profit =

                netAmount.subtract(
                        totalCost
                );

        if (
                netAmount != null
                        && netAmount.compareTo(
                        BigDecimal.ZERO
                ) > 0
        ) {

            this.profitMargin =

                    profit
                            .divide(
                                    netAmount,
                                    4,
                                    RoundingMode.HALF_UP
                            )
                            .multiply(
                                    new BigDecimal(
                                            "100"
                                    )
                            );

        } else {

            this.profitMargin =
                    BigDecimal.ZERO;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(
            Long id
    ) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(
            String orderId
    ) {
        this.orderId = orderId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(
            String sku
    ) {
        this.sku = sku;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(
            String productName
    ) {
        this.productName = productName;
    }

    public Marketplace getMarketplace() {
        return marketplace;
    }

    public void setMarketplace(
            Marketplace marketplace
    ) {
        this.marketplace = marketplace;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(
            Integer quantity
    ) {
        this.quantity = quantity;
    }

    public BigDecimal getGrossAmount() {
        return grossAmount;
    }

    public void setGrossAmount(
            BigDecimal grossAmount
    ) {
        this.grossAmount = grossAmount;
    }

    public BigDecimal getNetAmount() {
        return netAmount;
    }

    public void setNetAmount(
            BigDecimal netAmount
    ) {
        this.netAmount = netAmount;
    }

    public BigDecimal getUnitSalePrice() {
        return unitSalePrice;
    }

    public void setUnitSalePrice(
            BigDecimal unitSalePrice
    ) {
        this.unitSalePrice = unitSalePrice;
    }

    public BigDecimal getProductCost() {
        return productCost;
    }

    public void setProductCost(
            BigDecimal productCost
    ) {
        this.productCost = productCost;
    }

    public BigDecimal getMarketplaceFee() {
        return marketplaceFee;
    }

    public void setMarketplaceFee(
            BigDecimal marketplaceFee
    ) {
        this.marketplaceFee = marketplaceFee;
    }

    public BigDecimal getShippingCost() {
        return shippingCost;
    }

    public void setShippingCost(
            BigDecimal shippingCost
    ) {
        this.shippingCost = shippingCost;
    }

    public BigDecimal getExtraCosts() {
        return extraCosts;
    }

    public void setExtraCosts(
            BigDecimal extraCosts
    ) {
        this.extraCosts = extraCosts;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(
            BigDecimal profit
    ) {
        this.profit = profit;
    }

    public BigDecimal getProfitMargin() {
        return profitMargin;
    }

    public void setProfitMargin(
            BigDecimal profitMargin
    ) {
        this.profitMargin = profitMargin;
    }

    public LocalDateTime getSoldAt() {
        return soldAt;
    }

    public void setSoldAt(
            LocalDateTime soldAt
    ) {
        this.soldAt = soldAt;
    }

    public String getMarketplaceItemId() {
        return marketplaceItemId;
    }

    public void setMarketplaceItemId(String marketplaceItemId) {
        this.marketplaceItemId = marketplaceItemId;
    }
}
