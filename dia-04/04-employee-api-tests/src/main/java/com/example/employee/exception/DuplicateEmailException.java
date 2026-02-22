package com.example.employee.exception;

public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Já existe um funcionário com o email: " + email);
    }
}
