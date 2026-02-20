package com.example.employee.exception;

public class SalaryBelowMinimumException extends RuntimeException {

    public SalaryBelowMinimumException() {
        super("Salário não pode ser menor que o salário mínimo (R$ 1.412,00)");
    }
}
