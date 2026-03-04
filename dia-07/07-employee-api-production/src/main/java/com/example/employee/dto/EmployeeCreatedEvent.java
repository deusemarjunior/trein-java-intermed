package com.example.employee.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Evento publicado no RabbitMQ quando um novo funcionário é criado.
 */
public record EmployeeCreatedEvent(
        Long employeeId,
        String name,
        String email,
        String departmentName,
        BigDecimal salary,
        LocalDateTime createdAt
) implements Serializable {}
