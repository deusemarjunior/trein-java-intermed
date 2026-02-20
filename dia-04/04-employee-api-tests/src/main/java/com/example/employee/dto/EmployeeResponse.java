package com.example.employee.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record EmployeeResponse(
        Long id,
        String name,
        String email,
        String cpf,
        BigDecimal salary,
        String departmentName,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
