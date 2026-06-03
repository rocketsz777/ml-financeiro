package br.com.vendas.mlfinanceiro.service.stock;

import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.domain.StockMovement;
import br.com.vendas.mlfinanceiro.domain.StockMovementType;
import br.com.vendas.mlfinanceiro.dto.StockEntryRequest;
import br.com.vendas.mlfinanceiro.repository.ProductRepository;
import br.com.vendas.mlfinanceiro.repository.StockMovementRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StockService {

    private final ProductRepository productRepository;

    private final StockMovementRepository stockMovementRepository;

    public StockService(
            ProductRepository productRepository,
            StockMovementRepository stockMovementRepository
    ) {

        this.productRepository =
                productRepository;

        this.stockMovementRepository =
                stockMovementRepository;
    }

    public void addStock(
            StockEntryRequest request
    ) {

        Product product =
                productRepository
                        .findBySku(
                                request.getSku()
                        )
                        .orElseThrow(
                                () -> new RuntimeException(
                                        "Produto não encontrado"
                                )
                        );

        product.setStockQuantity(
                product.getStockQuantity()
                        + request.getQuantity()
        );

        productRepository.save(
                product
        );

        StockMovement movement =
                new StockMovement();

        movement.setSku(
                request.getSku()
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

        stockMovementRepository.save(
                movement
        );
    }

    public List<StockMovement> listMovements() {

        return stockMovementRepository.findAll();
    }
}