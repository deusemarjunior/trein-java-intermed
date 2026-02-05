package com.example.solid;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class SolidDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SolidDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo() {
        return args -> {
            System.out.println("=".repeat(80));
            System.out.println("SOLID PRINCIPLES DEMO");
            System.out.println("=".repeat(80));
            System.out.println();
            
            System.out.println("📚 Este projeto demonstra os 5 princípios SOLID:");
            System.out.println();
            System.out.println("S - Single Responsibility Principle");
            System.out.println("O - Open/Closed Principle");
            System.out.println("L - Liskov Substitution Principle");
            System.out.println("I - Interface Segregation Principle");
            System.out.println("D - Dependency Inversion Principle");
            System.out.println();
            System.out.println("Explore os pacotes 'before' e 'after' de cada princípio!");
            System.out.println();
            System.out.println("=".repeat(80));
        };
    }
}
