package br.com.vendas.mlfinanceiro.domain;

public class MarketplaceListing {

    private String marketplace;

    private String marketplaceItemId;

    private String sellerSku;

    private String productSku;

    public MarketplaceListing() {
    }

    public MarketplaceListing(
            String marketplace,
            String marketplaceItemId,
            String sellerSku,
            String productSku
    ) {

        this.marketplace = marketplace;
        this.marketplaceItemId = marketplaceItemId;
        this.sellerSku = sellerSku;
        this.productSku = productSku;
    }

    public String getMarketplace() {

        return marketplace;
    }

    public void setMarketplace(
            String marketplace
    ) {

        this.marketplace = marketplace;
    }

    public String getMarketplaceItemId() {

        return marketplaceItemId;
    }

    public void setMarketplaceItemId(
            String marketplaceItemId
    ) {

        this.marketplaceItemId =
                marketplaceItemId;
    }

    public String getSellerSku() {

        return sellerSku;
    }

    public void setSellerSku(
            String sellerSku
    ) {

        this.sellerSku = sellerSku;
    }

    public String getProductSku() {

        return productSku;
    }

    public void setProductSku(
            String productSku
    ) {

        this.productSku = productSku;
    }
    public String toCsv() {

        return safe(marketplace) + ";" +
                safe(marketplaceItemId) + ";" +
                safe(sellerSku) + ";" +
                safe(productSku);
    }

    public static MarketplaceListing fromCsv(
            String line
    ) {

        String[] p = line.split(";", -1);

        return new MarketplaceListing(
                p[0],
                p[1],
                p[2],
                p[3]
        );
    }

    private static String safe(String v) {

        return v == null ? "" :
                v.replace(";", ",");
    }
}