package br.com.vendas.mlfinanceiro.service.file;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FileStoreService {

    private final Path dataDir;

    public FileStoreService() {

        this.dataDir =
                Paths.get("data");

        try {

            Files.createDirectories(
                    dataDir
            );

        } catch (IOException e) {

            throw new RuntimeException(
                    "Erro ao criar diretorio data",
                    e
            );
        }
    }

    public Path getDataDir() {

        return dataDir;
    }
}
