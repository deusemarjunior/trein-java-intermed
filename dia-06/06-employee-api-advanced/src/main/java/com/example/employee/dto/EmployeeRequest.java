package com.example.employee.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO de entrada para criação/atualização de funcionários.
 */
public record EmployeeRequest(
        @NotBlank(message = "Nome é obrigatório")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email inválido")
        String email,

        @NotBlank(message = "CPF é obrigatório")
        String cpf,

        @NotNull(message = "Salário é obrigatório")
        @DecimalMin(value = "0.01", message = "Salário deve ser maior que zero")
        BigDecimal salary,

        @NotNull(message = "Departamento é obrigatório")
        Long departmentId
) {}
