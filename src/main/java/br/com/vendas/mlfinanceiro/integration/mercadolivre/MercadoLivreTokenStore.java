package br.com.vendas.mlfinanceiro.integration.mercadolivre;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;

@Component
public class MercadoLivreTokenStore {

    private static final String FILE_NAME =
            "mercadolivre-token.json";

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    private MercadoLivreTokenResponse token;

    public void save(
            MercadoLivreTokenResponse token
    ) {

        this.token = token;

        try {

            File file =
                    new File(FILE_NAME);

            if (!file.exists()) {

                file.createNewFile();
            }

            objectMapper.writeValue(
                    file,
                    token
            );

            System.out.println(
                    "TOKEN SALVO COM SUCESSO"
            );

        } catch (IOException e) {

            System.out.println(
                    "ERRO AO SALVAR TOKEN"
            );

            e.printStackTrace();
        }
    }

    public MercadoLivreTokenResponse getToken() {

        if (token != null) {

            return token;
        }

        try {

            File file =
                    new File(FILE_NAME);

            if (file.exists()) {

                token =
                        objectMapper.readValue(
                                file,
                                MercadoLivreTokenResponse.class
                        );

                System.out.println(
                        "TOKEN CARREGADO"
                );
            }

        } catch (IOException e) {

            System.out.println(
                    "ERRO AO CARREGAR TOKEN"
            );

            e.printStackTrace();
        }

        return token;
    }

    public boolean hasToken() {

        return getToken() != null;
    }

    public void clear() {

        token = null;

        File file =
                new File(FILE_NAME);

        if (file.exists()) {

            file.delete();
        }
    }
}