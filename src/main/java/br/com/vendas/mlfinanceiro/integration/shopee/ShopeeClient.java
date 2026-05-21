package br.com.vendas.mlfinanceiro.integration.shopee;

import org.springframework.stereotype.Component;

@Component
public class ShopeeClient {

    public void authenticate() {

        System.out.println(
                "Autenticando Shopee..."
        );
    }

    public void getOrders() {

        System.out.println(
                "Buscando pedidos Shopee..."
        );
    }
}