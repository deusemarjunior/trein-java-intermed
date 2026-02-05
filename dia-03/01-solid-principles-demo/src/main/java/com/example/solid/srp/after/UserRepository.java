package com.example.solid.srp.after;

import org.springframework.stereotype.Repository;

/**
 * ✅ APLICANDO SRP
 * Responsabilidade ÚNICA: Persistir usuários no banco de dados
 */
@Repository
public class UserRepository {
    
    public void save(User user) {
        // Lógica de persistência no banco de dados
        System.out.println("Salvando usuário no banco: " + user.getEmail());
        
        // Em uma aplicação real, usaria JPA/JDBC aqui
        // entityManager.persist(user);
    }
}
