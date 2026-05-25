package br.com.vendas.mlfinanceiro.service;

import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.domain.StockMovement;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileStoreService {

    private static final String PRODUCTS_FILE =
            "produtos.csv";

    private static final String SALES_FILE =
            "vendas.csv";

    private static final String STOCK_MOVEMENTS_FILE =
            "stock_movements.csv";

    private final Path dataDir;

    public FileStoreService() {

        this.dataDir =
                Paths.get("data");

        try {

            Files.createDirectories(
                    dataDir
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao criar diretório data",
                    e
            );
        }
    }

    public Path getDataDir() {

        return dataDir;
    }

    public List<Product> loadProducts() {

        Path path =
                dataDir.resolve(
                        PRODUCTS_FILE
                );

        if (!Files.exists(path)) {

            return new ArrayList<Product>();
        }

        try {

            return Files.readAllLines(path)
                    .stream()
                    .skip(1)
                    .filter(
                            line -> !line.trim().isEmpty()
                    )
                    .map(
                            Product::fromCsv
                    )
                    .collect(Collectors.toList());

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao carregar produtos",
                    e
            );
        }
    }

    public void saveProducts(
            List<Product> products
    ) {

        Path path =
                dataDir.resolve(
                        PRODUCTS_FILE
                );

        List<String> lines =
                new ArrayList<String>();

        lines.add(
                "sku;mlItemId;name;costPrice;stock"
        );

        for (Product product : products) {

            lines.add(
                    product.toCsv()
            );
        }

        try {

            Files.write(
                    path,
                    lines
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao salvar produtos",
                    e
            );
        }
    }

    public List<Sale> loadSales() {

        Path path =
                dataDir.resolve(
                        SALES_FILE
                );

        if (!Files.exists(path)) {

            return new ArrayList<Sale>();
        }

        try {

            return Files.readAllLines(path)
                    .stream()
                    .skip(1)
                    .filter(
                            line -> !line.trim().isEmpty()
                    )
                    .map(
                            Sale::fromCsv
                    )
                    .collect(Collectors.toList());

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao carregar vendas",
                    e
            );
        }
    }

    public void saveSales(
            List<Sale> sales
    ) {

        Path path =
                dataDir.resolve(
                        SALES_FILE
                );

        List<String> lines =
                new ArrayList<String>();

        lines.add(
                "orderId;sku;productName;marketplace;quantity;grossAmount;netAmount;unitSalePrice;productCost;extraCosts;marketplaceFee;shippingCost;profit;soldAt"
        );

        for (Sale sale : sales) {

            lines.add(
                    sale.toCsv()
            );
        }

        try {

            Files.write(
                    path,
                    lines
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao salvar vendas",
                    e
            );
        }
    }

    public List<StockMovement> loadStockMovements() {

        Path path =
                dataDir.resolve(
                        STOCK_MOVEMENTS_FILE
                );

        if (!Files.exists(path)) {

            return new ArrayList<StockMovement>();
        }

        try {

            return Files.readAllLines(path)
                    .stream()
                    .skip(1)
                    .filter(
                            line -> !line.trim().isEmpty()
                    )
                    .map(
                            StockMovement::fromCsv
                    )
                    .collect(Collectors.toList());

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao carregar movimentações de estoque",
                    e
            );
        }
    }

    public void saveStockMovements(
            List<StockMovement> movements
    ) {

        Path path =
                dataDir.resolve(
                        STOCK_MOVEMENTS_FILE
                );

        List<String> lines =
                new ArrayList<String>();

        lines.add(
                "sku;type;quantity;reference;createdAt"
        );

        for (StockMovement movement : movements) {

            lines.add(
                    movement.toCsv()
            );
        }

        try {

            Files.write(
                    path,
                    lines
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao salvar movimentações",
                    e
            );
        }
    }
}