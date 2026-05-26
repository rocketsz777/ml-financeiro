package br.com.vendas.mlfinanceiro.integration.shopee;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class ShopeeClient {

    private final RestTemplate restTemplate;

    public ShopeeClient() {

        this.restTemplate =
                new RestTemplate();
    }

    public String getOrders(
            String accessToken,
            String shopId
    ) {

        String url =
                "https://partner.shopeemobile.com/api/v2/order/get_order_list";

        return restTemplate.getForObject(
                url,
                String.class
        );
    }
}