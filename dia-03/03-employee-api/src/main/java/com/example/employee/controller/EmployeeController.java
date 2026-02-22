package com.example.employee.controller;

import com.example.employee.model.Employee;
import com.example.employee.repository.EmployeeRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller básico — retorna a Entity diretamente.
 * 
 * ⚠️ PROBLEMAS ATUAIS:
 * - Retorna Entity JPA diretamente (sem DTO)
 * - Sem validação de entrada
 * - Sem tratamento de erros centralizado
 * - Sem regras de negócio (salário mínimo, email único)
 * - Estrutura de pacotes simples (sem hexagonal)
 * 
 * ============================================
 * TODO 1: Criar EmployeeRequest e EmployeeResponse (DTOs) para entrada e saída da API
 *   - EmployeeRequest: name, email, salary, cpf, departmentId
 *   - EmployeeResponse: id, name, email, salary, cpf, departmentName, createdAt
 * 
 * TODO 2: Criar EmployeeMapper com métodos toEntity() e toResponse()
 *   - toEntity(EmployeeRequest, Department) → Employee
 *   - toResponse(Employee) → EmployeeResponse
 * 
 * TODO 3: Implementar EmployeeService com regras de negócio:
 *   - Salário não pode ser menor que 1412.00
 *   - Email deve ser único (verificar antes de salvar)
 *   - Nome deve ter pelo menos 3 caracteres
 * 
 * TODO 4: Criar GlobalExceptionHandler com @ControllerAdvice
 *   - Tratar MethodArgumentNotValidException (erros de @Valid)
 *   - Tratar EmployeeNotFoundException (404)
 *   - Tratar DuplicateEmailException (409)
 *   - Retornar respostas no formato Problem Details (RFC 7807)
 * 
 * TODO 5: Adicionar Bean Validation nos DTOs:
 *   - @NotBlank no nome, @Email no email, @Positive no salário
 * 
 * TODO 6: Criar custom validator @ValidCpf que valida formato e dígitos
 * 
 * TODO 7: Refatorar pacotes para estrutura hexagonal:
 *   - Mover regras de negócio para domain/
 *   - Controller e DTOs para adapter/in/web/
 *   - Repository e Entity JPA para adapter/out/persistence/
 * ============================================
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeRepository employeeRepository;

    public EmployeeController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    // ❌ Retorna Entity diretamente — substituir por DTO (TODO 1, 2, 3)
    @GetMapping
    public ResponseEntity<List<Employee>> findAll() {
        return ResponseEntity.ok(employeeRepository.findAll());
    }

    // ❌ Retorna Entity diretamente — substituir por DTO (TODO 1, 2, 3)
    @GetMapping("/{id}")
    public ResponseEntity<Employee> findById(@PathVariable Long id) {
        return employeeRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ❌ Recebe Entity como request — substituir por DTO com @Valid (TODO 1, 5)
    // ❌ Sem regras de negócio — mover para Service (TODO 3)
    @PostMapping
    public ResponseEntity<Employee> create(@RequestBody Employee employee) {
        Employee saved = employeeRepository.save(employee);
        return ResponseEntity.status(201).body(saved);
    }

    // ❌ Recebe Entity como request — substituir por DTO com @Valid
    @PutMapping("/{id}")
    public ResponseEntity<Employee> update(@PathVariable Long id, @RequestBody Employee employee) {
        return employeeRepository.findById(id)
                .map(existing -> {
                    existing.setName(employee.getName());
                    existing.setEmail(employee.getEmail());
                    existing.setCpf(employee.getCpf());
                    existing.setSalary(employee.getSalary());
                    Employee updated = employeeRepository.save(existing);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!employeeRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        employeeRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
