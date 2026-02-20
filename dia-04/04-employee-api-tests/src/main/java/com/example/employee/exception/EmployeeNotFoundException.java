package com.example.employee.exception;

public class EmployeeNotFoundException extends RuntimeException {

    public EmployeeNotFoundException(Long id) {
        super("Funcionário não encontrado com id: " + id);
    }

    public EmployeeNotFoundException(String message) {
        super(message);
    }
}
