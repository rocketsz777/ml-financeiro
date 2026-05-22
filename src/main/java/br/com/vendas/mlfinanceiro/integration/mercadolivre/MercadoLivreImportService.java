package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.domain.Sale;
import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResult;
import br.com.vendas.mlfinanceiro.service.FileStoreService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class MercadoLivreImportService {

    private final MercadoLivreClient mercadoLivreClient;

    private final FileStoreService fileStoreService;

    public MercadoLivreImportService(
            MercadoLivreClient mercadoLivreClient,
            FileStoreService fileStoreService
    ) {

        this.mercadoLivreClient =
                mercadoLivreClient;

        this.fileStoreService =
                fileStoreService;
    }

    public int importOrders() {

        List<MercadoLivreOrderResult> orders =
                mercadoLivreClient
                        .getOrders()
                        .getResults();

        List<Sale> sales =
                fileStoreService.loadSales();

        int imported = 0;

        for (MercadoLivreOrderResult order : orders) {

            boolean alreadyExists =
                    sales.stream()
                            .anyMatch(
                                    s -> order.getId()
                                            .toString()
                                            .equals(
                                                    s.getOrderId()
                                            )
                            );

            if (alreadyExists) {

                continue;
            }

            Sale sale = new Sale();

            sale.setOrderId(
                    order.getId().toString()
            );

            sale.setSku("ML-" + order.getId());

            sale.setProductName(
                    "Pedido Mercado Livre"
            );

            sale.setQuantity(1);

            sale.setUnitSalePrice(
                    order.getTotal_amount()
            );

            sale.setProductCost(
                    BigDecimal.ZERO
            );

            sale.setMarketplaceFee(
                    BigDecimal.ZERO
            );

            sale.setShippingCost(
                    BigDecimal.ZERO
            );

            sale.setProfit(
                    order.getTotal_amount()
            );

            sale.setSoldAt(
                    OffsetDateTime.parse(
                            order.getDate_created()
                    ).toLocalDateTime()
            );

            sales.add(sale);

            imported++;
        }

        fileStoreService.saveSales(
                sales
        );

        return imported;
    }
}