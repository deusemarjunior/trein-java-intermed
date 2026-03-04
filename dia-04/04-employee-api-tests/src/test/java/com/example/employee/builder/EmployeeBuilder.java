package com.example.employee.builder;

import com.example.employee.dto.EmployeeRequest;
import com.example.employee.model.Department;
import com.example.employee.model.Employee;

import java.math.BigDecimal;

/**
 * ============================================
 * TODO 1: Implementar o EmployeeBuilder
 * ============================================
 *
 * Crie um Data Builder para facilitar a criação de objetos de teste.
 * O Builder deve ter valores padrão para todos os campos e métodos
 * withXxx() para customizar cada campo.
 *
 * Valores padrão sugeridos:
 *   - id: 1L
 *   - name: "Ana Silva"
 *   - email: "ana.silva@email.com"
 *   - cpf: "123.456.789-09"
 *   - salary: new BigDecimal("5000.00")
 *   - department: new Department(1L, "Engenharia")
 *
 * Métodos a implementar:
 *   - withId(Long id)
 *   - withName(String name)
 *   - withEmail(String email)
 *   - withCpf(String cpf)
 *   - withSalary(BigDecimal salary)
 *   - withDepartment(Department department)
 *   - build() → Employee
 *   - buildRequest() → EmployeeRequest
 *
 * Exemplo de uso esperado:
 *   Employee emp = new EmployeeBuilder().build();                    // valores padrão
 *   Employee emp2 = new EmployeeBuilder().withName("Carlos").build(); // nome customizado
 *   EmployeeRequest req = new EmployeeBuilder().buildRequest();       // DTO request
 */
public class EmployeeBuilder {

    // TODO 1: Declarar campos com valores padrão

    // TODO 1: Implementar métodos withXxx() que retornam this

    // TODO 1: Implementar build() que retorna Employee

    // TODO 1: Implementar buildRequest() que retorna EmployeeRequest

}
