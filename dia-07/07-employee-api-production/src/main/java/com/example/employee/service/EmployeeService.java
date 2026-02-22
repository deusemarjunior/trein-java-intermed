package com.example.employee.service;

import com.example.employee.dto.EmployeeCreatedEvent;
import com.example.employee.dto.EmployeeRequest;
import com.example.employee.dto.EmployeeResponse;
import com.example.employee.exception.ResourceNotFoundException;
import com.example.employee.messaging.EmployeeEventPublisher;
import com.example.employee.model.Department;
import com.example.employee.model.Employee;
import com.example.employee.repository.DepartmentRepository;
import com.example.employee.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// TODO 7: Importe org.slf4j.MDC para contexto de logging
// import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * TODO 7: Adicione logging contextual nos métodos deste service.
 *
 * Para cada método, adicione:
 * - log.info() para eventos de negócio (início e sucesso)
 * - log.error() para situações de falha (not found, exceptions)
 * - MDC.put("employeeId", id) nos métodos que operam em um employee específico
 * - MDC.remove("employeeId") após uso
 * - Sempre use placeholders {} em vez de concatenação de strings
 *
 * Exemplo:
 *   MDC.put("employeeId", String.valueOf(id));
 *   log.info("Buscando funcionário por ID");
 *   // ... lógica ...
 *   MDC.remove("employeeId");
 */
@Service
public class EmployeeService {

    private static final Logger log = LoggerFactory.getLogger(EmployeeService.class);

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeEventPublisher eventPublisher;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository,
                           EmployeeEventPublisher eventPublisher) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        // TODO 7a: Adicione log.info("Listando todos os funcionários");
        List<Employee> employees = employeeRepository.findAllWithDepartment();
        // TODO 7b: Adicione log.debug("Total de funcionários encontrados: {}", employees.size());
        return employees.stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long id) {
        // TODO 7c: Adicione MDC.put("employeeId", String.valueOf(id));
        // TODO 7c: Adicione log.info("Buscando funcionário por ID");

        Employee employee = employeeRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> {
                    // TODO 7d: Adicione log.error("Funcionário não encontrado com ID: {}", id);
                    return new ResourceNotFoundException("Employee", id);
                });

        // TODO 7c: Adicione MDC.remove("employeeId");
        return EmployeeResponse.from(employee);
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        // TODO 7e: Adicione log.info("Criando funcionário: {}", request.name());

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> {
                    // TODO 7f: Adicione log.error("Departamento não encontrado: {}", request.departmentId());
                    return new ResourceNotFoundException("Department", request.departmentId());
                });

        Employee employee = new Employee(
                request.name(),
                request.email(),
                request.cpf(),
                request.salary(),
                department
        );

        Employee saved = employeeRepository.save(employee);
        // TODO 7g: Adicione MDC.put("employeeId", String.valueOf(saved.getId()));
        // TODO 7g: Adicione log.info("Funcionário criado com sucesso: {} ({})", saved.getName(), saved.getEmail());

        // Publicar evento no RabbitMQ
        EmployeeCreatedEvent event = new EmployeeCreatedEvent(
                saved.getId(),
                saved.getName(),
                saved.getEmail(),
                department.getName(),
                saved.getSalary(),
                LocalDateTime.now()
        );
        eventPublisher.publishEmployeeCreated(event);

        // TODO 7g: Adicione MDC.remove("employeeId");
        return EmployeeResponse.from(saved);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        // TODO 7h: Adicione MDC.put("employeeId", String.valueOf(id));
        // TODO 7h: Adicione log.info("Atualizando funcionário: {}", request.name());

        Employee employee = employeeRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));

        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setCpf(request.cpf());
        employee.setSalary(request.salary());
        employee.setDepartment(department);

        Employee updated = employeeRepository.save(employee);
        // TODO 7h: Adicione log.info("Funcionário atualizado com sucesso");
        // TODO 7h: Adicione MDC.remove("employeeId");

        return EmployeeResponse.from(updated);
    }

    @Transactional
    public void delete(Long id) {
        // TODO 7i: Adicione MDC.put("employeeId", String.valueOf(id));
        // TODO 7i: Adicione log.info("Deletando funcionário");

        if (!employeeRepository.existsById(id)) {
            // TODO 7i: Adicione log.error("Tentativa de deletar funcionário inexistente: {}", id);
            throw new ResourceNotFoundException("Employee", id);
        }

        employeeRepository.deleteById(id);
        // TODO 7i: Adicione log.info("Funcionário deletado com sucesso");
        // TODO 7i: Adicione MDC.remove("employeeId");
    }
}
