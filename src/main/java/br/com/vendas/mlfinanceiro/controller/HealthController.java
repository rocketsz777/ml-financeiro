package br.com.vendas.mlfinanceiro.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/")
    public Map<String, Object> home() {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "status",
                "UP"
        );

        response.put(
                "application",
                "ml-financeiro"
        );

        response.put(
                "message",
                "API rodando com sucesso"
        );

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        return response;
    }

    @GetMapping("/health")
    public Map<String, Object> health() {

        Map<String, Object> response =
                new LinkedHashMap<>();

        response.put(
                "status",
                "UP"
        );

        response.put(
                "service",
                "health-check"
        );

        response.put(
                "timestamp",
                LocalDateTime.now()
        );

        return response;
    }
}