package com.example.employee.service;

import com.example.employee.dto.EmployeeCreatedEvent;
import com.example.employee.dto.EmployeeRequest;
import com.example.employee.dto.EmployeeResponse;
import com.example.employee.dto.EmployeeSummary;
import com.example.employee.exception.ResourceNotFoundException;
import com.example.employee.messaging.EmployeeEventPublisher;
import com.example.employee.model.Department;
import com.example.employee.model.Employee;
import com.example.employee.repository.DepartmentRepository;
import com.example.employee.repository.EmployeeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

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

    // ──────────────────────────────────────────────────────────────
    // TODO 1: Identifique o problema N+1 neste método.
    //
    // Execute GET /api/employees e observe nos logs quantas queries SQL são geradas.
    // Se houver 10 funcionários em 5 departamentos, quantas queries aparecem?
    //
    // Resposta esperada: 1 (employees) + N (departments) = N+1 queries
    // ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<EmployeeResponse> findAll() {
        log.info("📋 Listando todos os funcionários...");

        // ❌ PROBLEMA N+1: findAll() carrega Employee SEM Department.
        // Quando EmployeeResponse.from() acessa getDepartment().getName(),
        // Hibernate faz 1 query adicional por funcionário!
        List<Employee> employees = employeeRepository.findAll();

        // TODO 2: Substitua findAll() por findAllWithDepartment() após criá-lo no repository.
        // List<Employee> employees = employeeRepository.findAllWithDepartment();

        return employees.stream()
                .map(EmployeeResponse::from)
                .toList();
    }

    // ──────────────────────────────────────────────────────────────
    // TODO 4: Crie um endpoint paginado usando o EmployeeSummary.
    //
    // Implemente este método para retornar Page<EmployeeSummary>
    // usando o método findAllSummaries() do repository (TODO 3).
    // ──────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Page<EmployeeSummary> findSummaries(Pageable pageable) {
        log.info("📋 Listando funcionários com projeção DTO e paginação...");

        // TODO 4: Descomente após implementar findAllSummaries() no repository
        // return employeeRepository.findAllSummaries(pageable);

        // Placeholder — retorna página vazia até implementar
        return Page.empty(pageable);
    }

    @Transactional(readOnly = true)
    public EmployeeResponse findById(Long id) {
        Employee employee = employeeRepository.findByIdWithDepartment(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", id));
        return EmployeeResponse.from(employee);
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department", request.departmentId()));

        Employee employee = new Employee(
                request.name(),
                request.email(),
                request.cpf(),
                request.salary(),
                department
        );

        Employee saved = employeeRepository.save(employee);
        log.info("✅ Funcionário #{} criado: {}", saved.getId(), saved.getName());

        // TODO 6e: Descomente para publicar evento no RabbitMQ após criar funcionário
        // EmployeeCreatedEvent event = new EmployeeCreatedEvent(
        //         saved.getId(),
        //         saved.getName(),
        //         saved.getEmail(),
        //         department.getName(),
        //         saved.getSalary(),
        //         LocalDateTime.now()
        // );
        // eventPublisher.publishEmployeeCreated(event);

        return EmployeeResponse.from(saved);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
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
        log.info("✏️ Funcionário #{} atualizado", updated.getId());

        return EmployeeResponse.from(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new ResourceNotFoundException("Employee", id);
        }
        employeeRepository.deleteById(id);
        log.info("🗑️ Funcionário #{} deletado", id);
    }
}
