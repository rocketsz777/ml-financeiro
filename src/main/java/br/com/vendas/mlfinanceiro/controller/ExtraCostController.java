package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.domain.ExtraCost;
import br.com.vendas.mlfinanceiro.dto.ExtraCostRequest;
import br.com.vendas.mlfinanceiro.service.finance.ExtraCostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/extra-costs")
public class ExtraCostController {

    private final ExtraCostService service;

    public ExtraCostController(
            ExtraCostService service
    ) {
        this.service = service;
    }

    @GetMapping
    public List<ExtraCost> list() {

        return service.findAll();
    }

    @PostMapping
    public ResponseEntity<ExtraCost> create(
            @RequestBody ExtraCostRequest request
    ) {

        ExtraCost cost =
                new ExtraCost();

        cost.setName(
                request.getName()
        );

        cost.setValue(
                request.getValue()
        );

        cost.setActive(
                request.getActive()
        );

        return ResponseEntity.ok(
                service.save(cost)
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {

        service.delete(id);

        return ResponseEntity.noContent()
                .build();
    }
}