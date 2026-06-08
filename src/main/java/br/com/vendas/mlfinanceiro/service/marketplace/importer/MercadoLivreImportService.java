package br.com.vendas.mlfinanceiro.service.marketplace.importer;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.MercadoLivreClient;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResponse;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResult;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreOrderDetail;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreOrderItem;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreShipmentResponse;
import br.com.vendas.mlfinanceiro.repository.SaleRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoLivreImportService {

    private final MercadoLivreClient mercadoLivreClient;
    private final SaleRepository saleRepository;

    public MercadoLivreImportService(
            MercadoLivreClient mercadoLivreClient,
            SaleRepository saleRepository
    ) {
        this.mercadoLivreClient = mercadoLivreClient;
        this.saleRepository = saleRepository;
    }

    // Retorna contagem — mantido para compatibilidade com ImportController
    public int importOrders() {
        return importAndReturnSales().size();
    }

    // Importa e retorna a lista de vendas salvas
    // Usado por MercadoLivreIntegration.importSales()
    public List<Sale> importAndReturnSales() {

        List<Sale> savedSales = new ArrayList<>();

        System.out.println("=== IMPORT ML ===");

        MercadoLivreOrderResponse response =
                mercadoLivreClient.getOrders();

        if (response == null) {
            System.out.println("Response NULL");
            return savedSales;
        }

        if (response.getResults() == null) {
            System.out.println("Results NULL");
            return savedSales;
        }

        System.out.println(
                "Pedidos encontrados: "
                        + response.getResults().size()
        );

        for (MercadoLivreOrderResult order : response.getResults()) {

            System.out.println(
                    "Processando pedido: " + order.getId()
            );

            String orderId = String.valueOf(order.getId());

            boolean alreadyImported =
                    saleRepository.existsByOrderId(orderId);

            if (alreadyImported) {
                System.out.println(
                        "Pedido já existe: " + orderId
                );
                continue;
            }

            MercadoLivreOrderDetail detail =
                    mercadoLivreClient.getOrderById(order.getId());

            if (detail == null
                    || detail.getOrder_items() == null
                    || detail.getOrder_items().isEmpty()) {

                System.out.println(
                        "Pedido sem itens: " + orderId
                );
                continue;
            }

            MercadoLivreOrderItem firstItem =
                    detail.getOrder_items().get(0);

            // =================================================================
            // 🛠️ NOVA RESTRUTURAÇÃO DO CÁLCULO FINANCEIRO (MERCADO LIVRE)
            // =================================================================

            // 1. Tratamento do Preço Unitário e Quantidade
            BigDecimal unitPrice = firstItem.getUnit_price() != null
                    ? firstItem.getUnit_price()
                    : BigDecimal.ZERO;

            int quantity = firstItem.getQuantity() != null ? firstItem.getQuantity() : 1;

            // Valor Bruto Total do Item (Preço Unitário * Quantidade)
            BigDecimal grossAmount = unitPrice.multiply(BigDecimal.valueOf(quantity));

            // 2. Coleta da Tarifa de Venda da Plataforma (Comissão)
            BigDecimal marketplaceFee = firstItem.getSale_fee() != null
                    ? firstItem.getSale_fee()
                    : BigDecimal.ZERO;

// 3. Coleta do Custo de Frete (Buscando via API do recurso /shipments se houver)
            BigDecimal shippingCost = BigDecimal.ZERO;
            if (detail.getShipping() != null && detail.getShipping().getId() != null) {
                try {
                    // CORREÇÃO: Alterado de 'var' para o tipo explícito MercadoLivreShipmentResponse
                    MercadoLivreShipmentResponse shipmentData =
                            mercadoLivreClient.getShipmentById(detail.getShipping().getId());

                    if (shipmentData != null && shipmentData.getCosts() != null && shipmentData.getCosts().getSenderCost() != null) {
                        shippingCost = shipmentData.getCosts().getSenderCost();
                    }
                } catch (Exception e) {
                    System.err.println("Erro ao buscar frete para o envio " + detail.getShipping().getId() + ": " + e.getMessage());
                }
            }
            // 4. Matemática exata do Valor Líquido Real que entra na conta
            BigDecimal netAmount = grossAmount.subtract(marketplaceFee).subtract(shippingCost);

            // =================================================================
            // 💾 MAPEAMENTO INDIVIDUALIZADO DAS COLUNAS DO BANCO DE DADOS
            // =================================================================
            Sale sale = new Sale();

            sale.setOrderId(orderId);
            sale.setMarketplace(Marketplace.MERCADO_LIVRE);
            sale.setProductName(firstItem.getItem().getTitle());
            sale.setSku(firstItem.getItem().getId());
            sale.setQuantity(quantity);
            sale.setSoldAt(LocalDateTime.now());

            // Atribuição correta de cada indicador financeiro
            sale.setGrossAmount(grossAmount);         // Preço total dos produtos
            sale.setUnitSalePrice(unitPrice);         // Preço cobrado por unidade
            sale.setMarketplaceFee(marketplaceFee);   // Taxa de comissão do ML cobrada
            sale.setShippingCost(shippingCost);       // Custo de frete cobrado do vendedor
            sale.setNetAmount(netAmount);             // Valor líquido final consolidado 💎

            // Custos locais/internos (para preenchimento posterior se aplicável)
            sale.setExtraCosts(BigDecimal.ZERO);
            sale.setProductCost(BigDecimal.ZERO);

            // Calcula o Lucro Real Interno (netAmount - productCost - extraCosts)
            sale.calculateProfit();

            System.out.println(
                    "Salvando pedido: " + orderId + " | Valor Líquido Calculado: R$ " + netAmount
            );

            Sale saved = saleRepository.save(sale);
            savedSales.add(saved);
        }

        System.out.println(
                "Total importado: " + savedSales.size()
        );

        return savedSales;
    }
}