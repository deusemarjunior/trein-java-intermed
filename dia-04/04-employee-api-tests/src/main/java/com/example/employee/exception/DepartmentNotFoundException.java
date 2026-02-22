package com.example.employee.exception;

public class DepartmentNotFoundException extends RuntimeException {

    public DepartmentNotFoundException(Long id) {
        super("Departamento não encontrado com id: " + id);
    }
}
