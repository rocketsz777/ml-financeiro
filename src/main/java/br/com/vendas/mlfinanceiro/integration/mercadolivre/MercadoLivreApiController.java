package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import br.com.vendas.mlfinanceiro.integration.mercadolivre.dto.MercadoLivreOrderResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MercadoLivreApiController {

    private final MercadoLivreClient mercadoLivreClient;

    public MercadoLivreApiController(
            MercadoLivreClient mercadoLivreClient
    ) {

        this.mercadoLivreClient =
                mercadoLivreClient;
    }

    @GetMapping("/ml/me")
    public String me() {

        return mercadoLivreClient
                .getMyUserData();
    }

    @GetMapping("/ml/orders")
    public MercadoLivreOrderResponse orders() {

        return mercadoLivreClient
                .getOrders();
    }
}