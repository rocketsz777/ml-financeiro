package br.com.vendas.mlfinanceiro.exception;

public class IntegrationException extends RuntimeException {

    public IntegrationException(String message) {
        super(message);
    }
}