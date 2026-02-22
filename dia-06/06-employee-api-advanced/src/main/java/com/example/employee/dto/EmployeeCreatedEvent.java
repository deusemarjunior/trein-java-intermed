package com.example.employee.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * TODO 6: Evento publicado no RabbitMQ quando um novo funcionário é criado.
 *
 * Este record é enviado como mensagem JSON ao RabbitMQ.
 * O consumer receberá este evento e simulará o envio de um email de boas-vindas.
 *
 * Campos sugeridos:
 * - employeeId, name, email, departmentName, salary, createdAt
 */
public record EmployeeCreatedEvent(
        Long employeeId,
        String name,
        String email,
        String departmentName,
        BigDecimal salary,
        LocalDateTime createdAt
) implements Serializable {}
