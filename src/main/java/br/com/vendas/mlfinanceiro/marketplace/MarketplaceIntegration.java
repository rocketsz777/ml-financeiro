package br.com.vendas.mlfinanceiro.marketplace;

import br.com.vendas.mlfinanceiro.domain.Sale;

import java.util.List;

public interface MarketplaceIntegration {

    List<Sale> importSales();

}