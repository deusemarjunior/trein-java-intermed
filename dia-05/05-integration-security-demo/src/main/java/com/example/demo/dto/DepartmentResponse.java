package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Dados do departamento (serviço externo)")
public record DepartmentResponse(
        @Schema(description = "ID do departamento", example = "1")
        Long id,

        @Schema(description = "Nome do departamento", example = "Tecnologia da Informação")
        String name,

        @Schema(description = "Código do departamento", example = "TI")
        String code
) {}
