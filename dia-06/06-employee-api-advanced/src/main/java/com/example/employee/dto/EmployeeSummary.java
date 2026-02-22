package com.example.employee.dto;

import java.math.BigDecimal;

/**
 * TODO 3: Projeção DTO usando Java Record.
 *
 * Este record deve ser usado na query JPQL com SELECT NEW para evitar N+1.
 * Busca apenas os campos necessários diretamente no banco, sem carregar
 * a entidade Employee completa.
 *
 * Campos:
 * - id (Long): ID do funcionário
 * - name (String): Nome do funcionário
 * - departmentName (String): Nome do departamento (via JOIN)
 *
 * Exemplo de query JPQL:
 *   SELECT new com.example.employee.dto.EmployeeSummary(e.id, e.name, d.name)
 *   FROM Employee e JOIN e.department d
 *
 * Implemente o record abaixo:
 */
public record EmployeeSummary(
        // TODO 3: Defina os campos do record: id, name, departmentName
        Long id,
        String name,
        String departmentName
) {}
