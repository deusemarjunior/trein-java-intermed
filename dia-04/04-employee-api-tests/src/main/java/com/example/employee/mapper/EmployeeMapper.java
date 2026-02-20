package com.example.employee.mapper;

import com.example.employee.dto.EmployeeRequest;
import com.example.employee.dto.EmployeeResponse;
import com.example.employee.model.Department;
import com.example.employee.model.Employee;

public class EmployeeMapper {

    private EmployeeMapper() {
    }

    public static Employee toEntity(EmployeeRequest request, Department department) {
        return new Employee(
                request.name(),
                request.email(),
                request.cpf(),
                request.salary(),
                department
        );
    }

    public static EmployeeResponse toResponse(Employee employee) {
        return new EmployeeResponse(
                employee.getId(),
                employee.getName(),
                employee.getEmail(),
                employee.getCpf(),
                employee.getSalary(),
                employee.getDepartment() != null ? employee.getDepartment().getName() : null,
                employee.getCreatedAt(),
                employee.getUpdatedAt()
        );
    }

    public static void updateEntity(Employee employee, EmployeeRequest request, Department department) {
        employee.setName(request.name());
        employee.setEmail(request.email());
        employee.setCpf(request.cpf());
        employee.setSalary(request.salary());
        employee.setDepartment(department);
    }
}
