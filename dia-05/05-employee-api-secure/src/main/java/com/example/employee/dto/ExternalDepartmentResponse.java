package com.example.employee.dto;

/**
 * DTO para resposta do serviço externo de departamentos (Feign).
 */
public record ExternalDepartmentResponse(
        Long id,
        String name,
        String code,
        String description
) {
}
