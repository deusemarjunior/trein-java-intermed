package com.example.employee.dto;

import com.example.employee.model.Employee;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO de saída para funcionários.
 *
 * Observe que este DTO acessa employee.getDepartment().getName().
 * Sem JOIN FETCH, isso dispara uma query adicional para cada funcionário (N+1).
 */
public record EmployeeResponse(
        Long id,
        String name,
        String email,
        String cpf,
        BigDecimal salary,
        String departmentName,
        LocalDateTime createdAt
) {
    public static EmployeeResponse from(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getCpf(),
                employee.getSalary(),
                employee.getDepartment() != null ? employee.getDepartment().getName() : null,
                employee.getCreatedAt()
        );
    }
}
