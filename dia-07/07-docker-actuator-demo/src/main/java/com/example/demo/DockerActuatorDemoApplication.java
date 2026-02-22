package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class DockerActuatorDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DockerActuatorDemoApplication.class, args);
    }
}
