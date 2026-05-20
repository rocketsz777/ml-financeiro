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

    public ProductController(SalesService salesService) {
        this.salesService = salesService;
    }

    @GetMapping
    public List<Product> listar() {
        return salesService.listProducts();
    }

    @PostMapping
    public Product salvar(@RequestBody ProductRequest request) {
        Product p = new Product(request.getSku(), request.getMlItemId(), request.getName(), request.getCostPrice(), request.getStock());
        return salesService.createOrUpdateProduct(p);
    }

    @DeleteMapping("/{sku}")
    public ResponseEntity<Void> excluir(@PathVariable String sku) {
        salesService.deleteProduct(sku);
        return ResponseEntity.noContent().build();
    }
}
