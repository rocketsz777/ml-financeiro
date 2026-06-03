package br.com.vendas.mlfinanceiro.controller;

import br.com.vendas.mlfinanceiro.dto.SystemStatusResponse;
import br.com.vendas.mlfinanceiro.service.system.SystemService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SystemController {

    private final SystemService systemService;

    public SystemController(
            SystemService systemService
    ) {

        this.systemService =
                systemService;
    }

    @GetMapping("/api/system/ping")
    public String ping() {

        return "OK";
    }

    @GetMapping("/api/system/status")
    public SystemStatusResponse status() {

        return systemService
                .getStatus();
    }
}