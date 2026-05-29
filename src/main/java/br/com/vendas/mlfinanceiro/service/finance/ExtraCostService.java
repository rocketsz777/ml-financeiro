package br.com.vendas.mlfinanceiro.service.finance;

import br.com.vendas.mlfinanceiro.domain.ExtraCost;
import br.com.vendas.mlfinanceiro.repository.ExtraCostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExtraCostService {

    private final ExtraCostRepository repository;

    public ExtraCostService(
            ExtraCostRepository repository
    ) {
        this.repository = repository;
    }

    public List<ExtraCost> findAll() {

        return repository.findAll();
    }

    public List<ExtraCost> findActive() {

        return repository.findByActiveTrue();
    }

    public ExtraCost save(
            ExtraCost cost
    ) {

        return repository.save(cost);
    }

    public void delete(
            Long id
    ) {

        repository.deleteById(id);
    }
}