package br.com.vendas.mlfinanceiro.marketplace;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;

import java.util.List;

public interface MarketplaceIntegration {

    Marketplace getMarketplace();

    List<Sale> importSales();
}