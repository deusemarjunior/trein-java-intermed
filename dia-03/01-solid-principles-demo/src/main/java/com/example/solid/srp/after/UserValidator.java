package com.example.solid.srp.after;

import org.springframework.stereotype.Component;

/**
 * ✅ APLICANDO SRP
 * Responsabilidade ÚNICA: Validar dados de usuário
 */
@Component
public class UserValidator {
    
    public void validate(String name, String email, String password) {
        validateName(name);
        validateEmail(email);
        validatePassword(password);
    }
    
    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome inválido");
        }
    }
    
    private void validateEmail(String email) {
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }
    
    private void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new IllegalArgumentException("Senha deve ter pelo menos 8 caracteres");
        }
    }
}
