package com.example.movieservice.domain.exception;

public class ExternalServiceException extends RuntimeException {

    public ExternalServiceException(String service, String message) {
        super("Erro ao comunicar com " + service + ": " + message);
    }

    public ExternalServiceException(String service, String message, Throwable cause) {
        super("Erro ao comunicar com " + service + ": " + message, cause);
    }
}
