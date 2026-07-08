package br.com.vendas.mlfinanceiro.service.sales;

import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SalesService {

    private final SaleRepository saleRepository;

    public SalesService(

            SaleRepository saleRepository
    ) {

        this.saleRepository =
                saleRepository;
    }

    public List<Sale> findAll() {

        return saleRepository.findAll();
    }
}
