package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.domain.Product;
import br.com.vendas.mlfinanceiro.dto.ProductRequest;
import br.com.vendas.mlfinanceiro.service.SalesService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
public class ProductController {

    private final SalesService salesService;

    public ProductController(
            SalesService salesService
    ) {

        this.salesService =
                salesService;
    }

    @GetMapping
    public ResponseEntity<List<Product>> listar() {

        return ResponseEntity.ok(
                salesService.listProducts()
        );
    }

    @PostMapping
    public ResponseEntity<Product> salvar(
            @RequestBody ProductRequest request
    ) {

        Product product =
                new Product(
                        request.getSku(),
                        request.getMlItemId(),
                        request.getName(),
                        request.getCostPrice(),
                        request.getStock()
                );

        Product saved =
                salesService
                        .createOrUpdateProduct(
                                product
                        );

        return ResponseEntity.ok(
                saved
        );
    }

    @PutMapping("/{sku}")
    public ResponseEntity<Product> atualizar(
            @PathVariable String sku,
            @RequestBody ProductRequest request
    ) {

        Product product =
                new Product(
                        sku,
                        request.getMlItemId(),
                        request.getName(),
                        request.getCostPrice(),
                        request.getStock()
                );

        Product updated =
                salesService
                        .createOrUpdateProduct(
                                product
                        );

        return ResponseEntity.ok(
                updated
        );
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> excluir(
            @PathVariable String sku
    ) {

        salesService.deleteProduct(
                sku
        );

        return ResponseEntity.noContent()
                .build();
    }
}