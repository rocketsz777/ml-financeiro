package br.com.vendas.mlfinanceiro.service;

import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.domain.Sale;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class FileStoreService {

    private final Path dataDir;
    private final Path productsFile;
    private final Path salesFile;

    public FileStoreService(
            @Value("${app.data-dir:./data}") String dataDir
    ) {

        this.dataDir = Paths.get(dataDir)
                .toAbsolutePath()
                .normalize();

        this.productsFile =
                this.dataDir.resolve("produtos.csv");

        this.salesFile =
                this.dataDir.resolve("vendas.csv");
    }

    public void ensureFilesExist() throws IOException {

        Files.createDirectories(dataDir);

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
                    ("orderId;sku;productName;quantity;" +
                            "unitSalePrice;productCost;" +
                            "marketplaceFee;shippingCost;" +
                            "profit;soldAt\n")
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

                String line = lines.get(i);

                if (!line.trim().isEmpty()) {

                    products.add(
                            Product.fromCsv(line)
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

    public void saveProducts(List<Product> products) {

        try {

            StringBuilder sb =
                    new StringBuilder();

            sb.append(
                    "sku;mlItemId;name;costPrice;stock\n"
            );

            for (Product p : products) {

                sb.append(p.toCsv())
                        .append("\n");
            }

            Files.write(
                    productsFile,
                    sb.toString().getBytes(StandardCharsets.UTF_8),
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

                String line = lines.get(i);

                if (!line.trim().isEmpty()) {

                    sales.add(
                            Sale.fromCsv(line)
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

    public void saveSales(List<Sale> sales) {

        try {

            StringBuilder sb =
                    new StringBuilder();

            sb.append(
                    "orderId;sku;productName;" +
                            "quantity;unitSalePrice;" +
                            "productCost;marketplaceFee;" +
                            "shippingCost;profit;soldAt\n"
            );

            for (Sale s : sales) {

                sb.append(s.toCsv())
                        .append("\n");
            }

            Files.write(
                    salesFile,
                    sb.toString().getBytes(StandardCharsets.UTF_8),
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

    public Path getDataDir() {
        return dataDir;
    }
}