package br.com.vendas.mlfinanceiro.service;

import br.com.vendas.mlfinanceiro.domain.MarketplaceListing;
import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.domain.Sale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileStoreService {

    private final Path dataDir;

    private final Path productsFile;

    private final Path salesFile;

    private final Path listingsFile;

    public FileStoreService(
            @Value("${app.data-dir:./data}")
            String dataDir
    ) {

        this.dataDir =
                Paths.get(dataDir)
                        .toAbsolutePath()
                        .normalize();

        this.productsFile =
                this.dataDir.resolve(
                        "produtos.csv"
                );

        this.salesFile =
                this.dataDir.resolve(
                        "vendas.csv"
                );

        this.listingsFile =
                this.dataDir.resolve(
                        "listings.csv"
                );
    }

    public void ensureFilesExist()
            throws IOException {

        Files.createDirectories(
                dataDir
        );

        if (Files.notExists(productsFile)) {

            Files.write(
                    productsFile,
                    "sku;mlItemId;name;costPrice;stock\n"
                            .getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE
            );
        }

        if (Files.notExists(salesFile)) {

            Files.write(
                    salesFile,
                    "orderId;sku;productName;marketplace;quantity;grossAmount;netAmount;unitSalePrice;productCost;extraCosts;marketplaceFee;shippingCost;profit;soldAt\n"
                            .getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE
            );
        }

        if (Files.notExists(listingsFile)) {

            Files.write(
                    listingsFile,
                    "marketplace;marketplaceItemId;sellerSku;productSku\n"
                            .getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.CREATE
            );
        }
    }

    public List<Product> loadProducts() {

        try {

            ensureFilesExist();

            List<String> lines =
                    Files.readAllLines(
                            productsFile,
                            StandardCharsets.UTF_8
                    );

            List<Product> products =
                    new ArrayList<Product>();

            for (int i = 1; i < lines.size(); i++) {

                if (!lines.get(i).trim().isEmpty()) {

                    products.add(
                            Product.fromCsv(
                                    lines.get(i)
                            )
                    );
                }
            }

            return products;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao ler produtos",
                    e
            );
        }
    }

    public void saveProducts(
            List<Product> products
    ) {

        try {

            StringBuilder sb =
                    new StringBuilder(
                            "sku;mlItemId;name;costPrice;stock\n"
                    );

            for (Product p : products) {

                sb.append(
                        p.toCsv()
                ).append("\n");
            }

            Files.write(
                    productsFile,
                    sb.toString()
                            .getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao salvar produtos",
                    e
            );
        }
    }

    public List<Sale> loadSales() {

        try {

            ensureFilesExist();

            List<String> lines =
                    Files.readAllLines(
                            salesFile,
                            StandardCharsets.UTF_8
                    );

            List<Sale> sales =
                    new ArrayList<Sale>();

            for (int i = 1; i < lines.size(); i++) {

                if (!lines.get(i).trim().isEmpty()) {

                    sales.add(
                            Sale.fromCsv(
                                    lines.get(i)
                            )
                    );
                }
            }

            return sales;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao ler vendas",
                    e
            );
        }
    }

    public void saveSales(
            List<Sale> sales
    ) {

        try {

            StringBuilder sb =
                    new StringBuilder(
                            "orderId;sku;productName;marketplace;quantity;grossAmount;netAmount;unitSalePrice;productCost;extraCosts;marketplaceFee;shippingCost;profit;soldAt\n"
                    );

            for (Sale s : sales) {

                sb.append(
                        s.toCsv()
                ).append("\n");
            }

            Files.write(
                    salesFile,
                    sb.toString()
                            .getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao salvar vendas",
                    e
            );
        }
    }

    public List<MarketplaceListing> loadListings() {

        try {

            ensureFilesExist();

            List<String> lines =
                    Files.readAllLines(
                            listingsFile,
                            StandardCharsets.UTF_8
                    );

            List<MarketplaceListing> listings =
                    new ArrayList<MarketplaceListing>();

            for (int i = 1; i < lines.size(); i++) {

                if (!lines.get(i).trim().isEmpty()) {

                    listings.add(
                            MarketplaceListing.fromCsv(
                                    lines.get(i)
                            )
                    );
                }
            }

            return listings;

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao ler listings",
                    e
            );
        }
    }

    public void saveListings(
            List<MarketplaceListing> listings
    ) {

        try {

            StringBuilder sb =
                    new StringBuilder(
                            "marketplace;marketplaceItemId;sellerSku;productSku\n"
                    );

            for (MarketplaceListing listing : listings) {

                sb.append(
                        listing.toCsv()
                ).append("\n");
            }

            Files.write(
                    listingsFile,
                    sb.toString()
                            .getBytes(StandardCharsets.UTF_8),
                    StandardOpenOption.TRUNCATE_EXISTING,
                    StandardOpenOption.CREATE
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao salvar listings",
                    e
            );
        }
    }

    public Path getDataDir() {

        return dataDir;
    }
}