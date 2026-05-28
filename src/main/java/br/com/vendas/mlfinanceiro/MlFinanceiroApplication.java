package br.com.vendas.mlfinanceiro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MlFinanceiroApplication {

    public static void main(String[] args) {

        SpringApplication.run(
                MlFinanceiroApplication.class,
                args
        );
    }
}