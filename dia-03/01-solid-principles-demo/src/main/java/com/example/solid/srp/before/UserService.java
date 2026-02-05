package com.example.solid.srp.before;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

/**
 * ❌ VIOLAÇÃO DO SRP (Single Responsibility Principle)
 * 
 * Esta classe tem MÚLTIPLAS responsabilidades:
 * 1. Lógica de negócio (criar usuário)
 * 2. Validação de dados
 * 3. Persistência no banco de dados
 * 4. Envio de email
 * 5. Logging
 * 
 * Problema: 5 razões diferentes para modificar esta classe!
 */
public class UserService {
    
    public void createUser(String name, String email, String password) {
        try {
            // Responsabilidade 1: Validação
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Nome inválido");
            }
            
            if (!email.contains("@")) {
                throw new IllegalArgumentException("Email inválido");
            }
            
            if (password.length() < 8) {
                throw new IllegalArgumentException("Senha deve ter pelo menos 8 caracteres");
            }
            
            // Responsabilidade 2: Lógica de negócio
            String hashedPassword = hashPassword(password);
            
            // Responsabilidade 3: Persistência no banco de dados
            Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/mydb", "user", "pass"
            );
            
            String sql = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setString(2, email);
            stmt.setString(3, hashedPassword);
            stmt.executeUpdate();
            
            stmt.close();
            conn.close();
            
            // Responsabilidade 4: Envio de email
            sendWelcomeEmail(email, name);
            
            // Responsabilidade 5: Logging
            System.out.println("User created: " + email);
            
        } catch (Exception e) {
            // Responsabilidade 6: Tratamento de erros
            System.err.println("Error creating user: " + e.getMessage());
            throw new RuntimeException("Failed to create user", e);
        }
    }
    
    private String hashPassword(String password) {
        // Simplificado para exemplo
        return "hashed_" + password;
    }
    
    private void sendWelcomeEmail(String email, String name) {
        // Lógica de envio de email
        System.out.println("Sending welcome email to " + email);
    }
}
