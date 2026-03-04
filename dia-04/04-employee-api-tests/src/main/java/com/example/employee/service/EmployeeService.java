package com.example.employee.service;

import com.example.employee.dto.EmployeeRequest;
import com.example.employee.dto.EmployeeResponse;
import com.example.employee.exception.DepartmentNotFoundException;
import com.example.employee.exception.DuplicateEmailException;
import com.example.employee.exception.EmployeeNotFoundException;
import com.example.employee.exception.SalaryBelowMinimumException;
import com.example.employee.mapper.EmployeeMapper;
import com.example.employee.model.Department;
import com.example.employee.model.Employee;
import com.example.employee.repository.DepartmentRepository;
import com.example.employee.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class EmployeeService {

    private static final BigDecimal SALARIO_MINIMO = new BigDecimal("1412.00");

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;

    public EmployeeService(EmployeeRepository employeeRepository,
                           DepartmentRepository departmentRepository) {
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
    }

    public List<EmployeeResponse> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(EmployeeMapper::toResponse)
                .toList();
    }

    public EmployeeResponse findById(Long id) {
        return employeeRepository.findById(id)
                .map(EmployeeMapper::toResponse)
                .orElseThrow(() -> new EmployeeNotFoundException(id));
    }

    @Transactional
    public EmployeeResponse create(EmployeeRequest request) {
        // Regra: salário mínimo
        validateSalary(request.salary());

        // Regra: email único
        if (employeeRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException(request.email());
        }

        // Busca departamento
        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(request.departmentId()));

        Employee employee = EmployeeMapper.toEntity(request, department);
        Employee saved = employeeRepository.save(employee);
        return EmployeeMapper.toResponse(saved);
    }

    @Transactional
    public EmployeeResponse update(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(id));

        // Regra: salário mínimo
        validateSalary(request.salary());

        // Regra: email único (para outro funcionário)
        employeeRepository.findByEmail(request.email())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateEmailException(request.email());
                });

        Department department = departmentRepository.findById(request.departmentId())
                .orElseThrow(() -> new DepartmentNotFoundException(request.departmentId()));

        EmployeeMapper.updateEntity(employee, request, department);
        Employee saved = employeeRepository.save(employee);
        return EmployeeMapper.toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EmployeeNotFoundException(id);
        }
        employeeRepository.deleteById(id);
    }

    private void validateSalary(BigDecimal salary) {
        if (salary.compareTo(SALARIO_MINIMO) < 0) {
            throw new SalaryBelowMinimumException();
        }
    }
}
