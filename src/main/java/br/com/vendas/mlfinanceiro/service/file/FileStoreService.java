package br.com.vendas.mlfinanceiro.service.file;

import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.domain.Sale;
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

    private static final String SALES_FILE =
            "vendas.csv";

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

    // =========================================
    // PRODUTOS
    // =========================================

    public List<Product> loadProducts() {

        return new ArrayList<Product>();
    }

    public void saveProducts(
            List<Product> products
    ) {

        // Produtos migrados para PostgreSQL
    }

    // =========================================
    // VENDAS
    // =========================================

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
                "orderId;sku;productName;marketplace;quantity;grossAmount;netAmount;unitSalePrice;productCost;extraCosts;marketplaceFee;shippingCost;profit;profitMargin;soldAt"
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
}