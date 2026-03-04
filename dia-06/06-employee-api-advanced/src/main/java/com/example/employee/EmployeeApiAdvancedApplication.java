package com.example.employee;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
// TODO 8: Adicione @EnableCaching para habilitar o cache Redis
// import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
// @EnableCaching  // TODO 8: Descomente esta linha
public class EmployeeApiAdvancedApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmployeeApiAdvancedApplication.class, args);
    }
}
