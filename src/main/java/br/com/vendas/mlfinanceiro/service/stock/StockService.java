package br.com.vendas.mlfinanceiro.service.stock;

import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.domain.StockMovement;
import br.com.vendas.mlfinanceiro.domain.StockMovementType;
import br.com.vendas.mlfinanceiro.dto.StockEntryRequest;
import br.com.vendas.mlfinanceiro.service.file.FileStoreService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class StockService {

    private final FileStoreService fileStoreService;

    public StockService(
            FileStoreService fileStoreService
    ) {

        this.fileStoreService =
                fileStoreService;
    }

    public void addStock(
            StockEntryRequest request
    ) {

        List<Product> products =
                fileStoreService.loadProducts();

        Product product =
                products.stream()
                        .filter(
                                p -> p.getSku()
                                        .equalsIgnoreCase(
                                                request.getSku()
                                        )
                        )
                        .findFirst()
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Produto não encontrado"
                                )
                        );

        product.setStockQuantity(
                product.getStockQuantity() +
                        request.getQuantity()
        );

        fileStoreService.saveProducts(
                products
        );

        List<StockMovement> movements =
                fileStoreService.loadStockMovements();

        StockMovement movement =
                new StockMovement();

        movement.setSku(
                product.getSku()
        );

        movement.setType(
                StockMovementType.ENTRY
        );

        movement.setQuantity(
                request.getQuantity()
        );

        movement.setReference(
                request.getReference()
        );

        movement.setCreatedAt(
                LocalDateTime.now()
        );

        movements.add(
                movement
        );

        fileStoreService.saveStockMovements(
                movements
        );
    }

    public List<StockMovement> listMovements() {

        return fileStoreService
                .loadStockMovements();
    }
}