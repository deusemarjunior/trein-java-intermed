package com.example.employee.client;

import com.example.employee.exception.DepartmentNotFoundException;
import com.example.employee.exception.ExternalServiceException;
import feign.Response;
import feign.codec.ErrorDecoder;

/**
 * TODO 2: Implementar o ErrorDecoder customizado para o Feign Client.
 *
 * O decoder deve tratar os seguintes status HTTP:
 * - 404 → lançar DepartmentNotFoundException
 * - 503 → lançar ExternalServiceException (serviço indisponível)
 * - Outros → lançar ExternalServiceException com o status HTTP
 *
 * Dica: use switch expression (Java 21) para tratar os status.
 */
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        // TODO 2: Implementar o tratamento de erros
        //
        // return switch (response.status()) {
        //     case 404 -> new DepartmentNotFoundException(
        //             "Departamento não encontrado no serviço externo");
        //     case 503 -> new ExternalServiceException(
        //             "Serviço de departamentos indisponível");
        //     default -> new ExternalServiceException(
        //             "Erro ao comunicar com serviço externo: HTTP " + response.status());
        // };

        return new ExternalServiceException(
                "Erro ao comunicar com serviço externo: HTTP " + response.status());
    }
}
