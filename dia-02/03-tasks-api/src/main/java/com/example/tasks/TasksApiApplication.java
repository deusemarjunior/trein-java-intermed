package com.example.tasks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TasksApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TasksApiApplication.class, args);
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════╗
            ║  Tasks API - Exercício 2                                ║
            ║  Evolução: Memória → Banco de Dados                     ║
            ║                                                          ║
            ║  🌐 http://localhost:8082                               ║
            ║  📊 H2 Console: http://localhost:8082/h2-console        ║
            ║                                                          ║
            ║  Conceitos: Paginação, DTOs, Validação, Exceptions     ║
            ╚══════════════════════════════════════════════════════════╝
            """);
    }
}
