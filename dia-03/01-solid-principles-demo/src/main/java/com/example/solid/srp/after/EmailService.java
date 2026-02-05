package com.example.solid.srp.after;

import org.springframework.stereotype.Service;

/**
 * ✅ APLICANDO SRP
 * Responsabilidade ÚNICA: Enviar emails
 */
@Service
public class EmailService {
    
    public void sendWelcomeEmail(String email, String name) {
        System.out.println("Enviando email de boas-vindas para: " + email);
        
        // Em uma aplicação real, usaria JavaMailSender ou API de email
        String subject = "Bem-vindo!";
        String body = "Olá " + name + ", seja bem-vindo!";
        
        System.out.println("Subject: " + subject);
        System.out.println("Body: " + body);
    }
}
