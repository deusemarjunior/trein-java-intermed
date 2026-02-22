package com.example.employee.repository;

import com.example.employee.dto.EmployeeSummary;
import com.example.employee.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    /**
     * ❌ PROBLEMA N+1:
     * O método findAll() herdado de JpaRepository carrega Employee SEM Department.
     * Quando o EmployeeResponse.from() acessa employee.getDepartment().getName(),
     * Hibernate dispara 1 query adicional por funcionário.
     *
     * Se temos 10 funcionários em 3 departamentos:
     * - 1 query: SELECT * FROM employees
     * - 10 queries: SELECT * FROM departments WHERE id = ? (uma por employee)
     * Total: 11 queries! (1 + N)
     *
     * Execute GET /api/employees e conte as queries nos logs.
     */

    // ──────────────────────────────────────────────────────────────
    // TODO 2: Corrija o problema N+1 criando um método com JOIN FETCH.
    //
    // Crie o método:
    //   @Query("SELECT e FROM Employee e JOIN FETCH e.department")
    //   List<Employee> findAllWithDepartment();
    //
    // Depois, use este método no EmployeeService.findAll() em vez de findAll().
    // ──────────────────────────────────────────────────────────────



    // ──────────────────────────────────────────────────────────────
    // TODO 3: Crie a projeção DTO com SELECT NEW usando EmployeeSummary.
    //
    // Crie o método:
    //   @Query("""
    //       SELECT new com.example.employee.dto.EmployeeSummary(
    //           e.id, e.name, d.name
    //       )
    //       FROM Employee e JOIN e.department d
    //       """)
    //   Page<EmployeeSummary> findAllSummaries(Pageable pageable);
    //
    // ──────────────────────────────────────────────────────────────



    // Busca por ID com JOIN FETCH (já fornecido — sem N+1)
    @Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.id = :id")
    Optional<Employee> findByIdWithDepartment(Long id);

    Optional<Employee> findByEmail(String email);
}
