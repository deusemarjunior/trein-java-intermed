package com.example.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BlogApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(BlogApiApplication.class, args);
        System.out.println("""
            
            ╔══════════════════════════════════════════════════════════╗
            ║  Blog API - Exercício 1                                 ║
            ║  Foco: Relacionamentos JPA                              ║
            ║                                                          ║
            ║  🌐 http://localhost:8081                               ║
            ║  📊 H2 Console: http://localhost:8081/h2-console        ║
            ║                                                          ║
            ║  Conceitos: OneToMany, ManyToMany, Fetch Types          ║
            ╚══════════════════════════════════════════════════════════╝
            """);
    }
}
