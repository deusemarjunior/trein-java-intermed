package com.example.solid.srp.after;

import lombok.Data;

/**
 * ✅ MODELO DE DOMÍNIO
 * Responsabilidade: Representar um usuário
 */
@Data
public class User {
    private final String name;
    private final String email;
    private final String password;
}
