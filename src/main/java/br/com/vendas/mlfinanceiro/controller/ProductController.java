package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.dto.ProductPerformanceResponse;
import br.com.vendas.mlfinanceiro.dto.ProductRequest;
import br.com.vendas.mlfinanceiro.service.product.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/produtos")
public class ProductController {

    private final ProductService productService;

    public ProductController(
            ProductService productService
    ) {

        this.productService =
                productService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> listar() {

        return ResponseEntity.ok(
                productService.findAll()
        );
    }

    @GetMapping("/{sku}")
    public ResponseEntity<Product> buscar(
            @PathVariable String sku
    ) {

        return ResponseEntity.ok(
                productService.findBySku(
                        sku
                )
        );
    }

    @PostMapping
    public ResponseEntity<Product> salvar(
            @RequestBody ProductRequest request
    ) {

        Product product =
                new Product();

        product.setSku(
                request.getSku()
        );

        if (
                request.getMlItemId() == null
                        || request.getMlItemId().trim().isEmpty()
        ) {

            product.setMlItemId(null);

        } else {

            product.setMlItemId(
                    request.getMlItemId()
            );
        }

        product.setName(
                request.getName()
        );

        product.setCostPrice(
                request.getCostPrice()
        );

        product.setOldCostPrice(
                request.getOldCostPrice()
        );

        product.setStockQuantity(
                request.getStockQuantity()
        );

        return ResponseEntity.ok(
                productService.save(
                        product
                )
        );
    }

    @PutMapping("/{sku}")
    public ResponseEntity<Product> atualizar(
            @PathVariable String sku,
            @RequestBody ProductRequest request
    ) {

        Product product =
                productService.findBySku(
                        sku
                );

        product.setSku(
                request.getSku()
        );

        product.setMlItemId(
                request.getMlItemId()
        );

        product.setName(
                request.getName()
        );

        product.setCostPrice(
                request.getCostPrice()
        );

        product.setOldCostPrice(
                request.getOldCostPrice()
        );

        product.setStockQuantity(
                request.getStockQuantity()
        );

        return ResponseEntity.ok(
                productService.save(
                        product
                )
        );
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> excluir(
            @PathVariable String sku
    ) {

        productService.delete(
                sku
        );

        return ResponseEntity.noContent()
                .build();
    }

    @PostMapping("/{sku}/add-stock")
    public ResponseEntity<Product> addStock(
            @PathVariable String sku,
            @RequestParam Integer quantity
    ) {

        return ResponseEntity.ok(
                productService.addStock(
                        sku,
                        quantity
                )
        );
    }

    @PostMapping("/{sku}/remove-stock")
    public ResponseEntity<Product> removeStock(
            @PathVariable String sku,
            @RequestParam Integer quantity
    ) {

        return ResponseEntity.ok(
                productService.removeStock(
                        sku,
                        quantity
                )
        );
    }

    @GetMapping("/performance")
    public ResponseEntity<Map<String, ProductPerformanceResponse>> getPerformance(
            @RequestParam String period
    ) {

        return ResponseEntity.ok(
                productService.getPerformance(period)
        );
    }
}