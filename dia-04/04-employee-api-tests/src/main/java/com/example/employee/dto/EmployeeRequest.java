package com.example.employee.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record EmployeeRequest(

        @NotBlank(message = "Nome é obrigatório")
        @Size(min = 3, max = 100, message = "Nome deve ter entre 3 e 100 caracteres")
        String name,

        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ser válido")
        String email,

        @NotBlank(message = "CPF é obrigatório")
        @Pattern(regexp = "\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
                message = "CPF deve estar no formato 999.999.999-99")
        String cpf,

        @NotNull(message = "Salário é obrigatório")
        @DecimalMin(value = "1412.00", message = "Salário não pode ser menor que o salário mínimo (R$ 1.412,00)")
        BigDecimal salary,

        @NotNull(message = "Departamento é obrigatório")
        Long departmentId
) {
}
