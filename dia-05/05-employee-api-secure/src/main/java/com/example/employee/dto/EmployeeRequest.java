package com.example.employee.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

// TODO 8: Adicionar annotations @Schema do Swagger nos campos
public record EmployeeRequest(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "CPF é obrigatório")
        String cpf,

        @NotNull(message = "Salário é obrigatório")
        @Positive(message = "Salário deve ser positivo")
        BigDecimal salary,

        @NotNull(message = "ID do departamento é obrigatório")
        Long departmentId
) {
}
