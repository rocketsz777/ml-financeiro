package br.com.vendas.mlfinanceiro;

import br.com.vendas.mlfinanceiro.service.FileStoreService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class MlFinanceiroApplication {

    public static void main(String[] args) {
        SpringApplication.run(MlFinanceiroApplication.class, args);
    }

    @Bean
    CommandLineRunner init(FileStoreService fileStoreService) {
        return args -> fileStoreService.ensureFilesExist();
    }
}
