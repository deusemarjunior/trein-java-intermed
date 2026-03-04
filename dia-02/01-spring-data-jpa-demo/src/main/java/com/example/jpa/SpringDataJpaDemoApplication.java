package com.example.jpa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringDataJpaDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringDataJpaDemoApplication.class, args);
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════╗
            ║  Spring Data JPA Demo - Dia 02                          ║
            ║  API iniciada com sucesso!                              ║
            ║                                                          ║
            ║  🌐 http://localhost:8080                               ║
            ║  📊 H2 Console: http://localhost:8080/h2-console        ║
            ║  📖 Documentação: README.md                             ║
            ╚══════════════════════════════════════════════════════════╝
            """);
    }
}
