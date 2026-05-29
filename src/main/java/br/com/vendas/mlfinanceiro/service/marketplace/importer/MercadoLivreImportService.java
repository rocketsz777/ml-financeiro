package br.com.vendas.mlfinanceiro.service.marketplace.importer;

import br.com.vendas.mlfinanceiro.domain.Marketplace;
import br.com.vendas.mlfinanceiro.domain.Sale;

import br.com.vendas.mlfinanceiro.integration.mercadolivre.MercadoLivreClient;

import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResponse;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResult;

import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreOrderDetail;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivreOrderItem;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.orderdetail.MercadoLivrePayment;

import br.com.vendas.mlfinanceiro.repository.SaleRepository;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class MercadoLivreImportService {

    private final MercadoLivreClient mercadoLivreClient;

    private final SaleRepository saleRepository;

    public MercadoLivreImportService(
            MercadoLivreClient mercadoLivreClient,
            SaleRepository saleRepository
    ) {

        this.mercadoLivreClient =
                mercadoLivreClient;

        this.saleRepository =
                saleRepository;
    }

    public int importOrders() {

        int importedCount = 0;

        MercadoLivreOrderResponse response =
                mercadoLivreClient.getOrders();

        if (response == null
                || response.getResults() == null) {

            return 0;
        }

        for (MercadoLivreOrderResult order
                : response.getResults()) {

            String orderId =
                    String.valueOf(
                            order.getId()
                    );

            boolean alreadyImported =
                    saleRepository.existsByOrderId(
                            orderId
                    );

            if (alreadyImported) {

                continue;
            }

            MercadoLivreOrderDetail detail =
                    mercadoLivreClient.getOrderById(
                            order.getId()
                    );

            if (detail == null
                    || detail.getOrder_items() == null
                    || detail.getOrder_items().isEmpty()) {

                continue;
            }

            MercadoLivreOrderItem firstItem =
                    detail.getOrder_items().get(0);

            MercadoLivrePayment payment =
                    detail.getPayments() != null
                            && !detail.getPayments().isEmpty()
                            ? detail.getPayments().get(0)
                            : null;

            BigDecimal liquidAmount =
                    payment != null
                            && payment.getTransaction_amount() != null
                            ? payment.getTransaction_amount()
                            : BigDecimal.ZERO;

            Sale sale =
                    new Sale();

            sale.setOrderId(
                    orderId
            );

            sale.setMarketplace(
                    Marketplace.MERCADO_LIVRE
            );

            sale.setProductName(
                    firstItem.getItem().getTitle()
            );

            sale.setSku(
                    firstItem.getItem().getId()
            );

            sale.setQuantity(
                    firstItem.getQuantity()
            );

            sale.setSoldAt(
                    LocalDateTime.now()
            );

            sale.setGrossAmount(
                    liquidAmount
            );

            sale.setNetAmount(
                    liquidAmount
            );

            sale.setUnitSalePrice(
                    liquidAmount
            );

            sale.setMarketplaceFee(
                    BigDecimal.ZERO
            );

            sale.setShippingCost(
                    BigDecimal.ZERO
            );

            sale.setExtraCosts(
                    BigDecimal.ZERO
            );

            sale.setProductCost(
                    BigDecimal.ZERO
            );

            sale.calculateProfit();

            saleRepository.save(
                    sale
            );

            importedCount++;
        }

        return importedCount;
    }
}