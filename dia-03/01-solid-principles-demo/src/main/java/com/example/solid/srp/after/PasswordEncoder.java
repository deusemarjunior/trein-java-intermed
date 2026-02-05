package com.example.solid.srp.after;

import org.springframework.stereotype.Component;

/**
 * ✅ APLICANDO SRP
 * Responsabilidade ÚNICA: Criptografar senhas
 */
@Component
public class PasswordEncoder {
    
    public String encode(String password) {
        // Simplificado para exemplo
        // Em produção, use BCryptPasswordEncoder
        return "hashed_" + password;
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }
}
