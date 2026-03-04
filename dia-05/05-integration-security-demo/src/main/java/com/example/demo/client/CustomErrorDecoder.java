package com.example.demo.client;

import com.example.demo.exception.DepartmentNotFoundException;
import com.example.demo.exception.ExternalServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new DepartmentNotFoundException(
                    "Departamento não encontrado no serviço externo");
            case 503 -> new ExternalServiceException(
                    "Serviço de departamentos indisponível");
            default -> new ExternalServiceException(
                    "Erro ao comunicar com serviço externo: HTTP " + response.status());
        };
    }
}
