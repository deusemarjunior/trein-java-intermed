# Slide 10: Criando Primeiro Projeto

**Horário:** 13:15 - 13:30

---

## 🎬 DEMO AO VIVO: Spring Initializr

**1. Acesse:** https://start.spring.io/

**2. Configure:**
```
Project: Maven
Language: Java
Spring Boot: 3.2.x (última stable)
Packaging: Jar
Java: 17 ou 21

Group: com.example
Artifact: products-api
Name: products-api
Description: Products REST API
Package name: com.example.products
```

**3. Dependências:**
- Spring Web
- Spring Data JPA
- H2 Database (para começar)
- Lombok (opcional)
- Validation
- Spring Boot DevTools

**4. Generate → Download → Extrair → Abrir na IDE**

---

## Estrutura Gerada

```
products-api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/products/
│   │   │       └── ProductsApiApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── static/
│   │       └── templates/
│   └── test/
│       └── java/
│           └── com/example/products/
│               └── ProductsApiApplicationTests.java
├── pom.xml
└── README.md
```

---

## Arquivo Principal

```java
package com.example.products;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication  // ← Mágica acontece aqui!
public class ProductsApiApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(ProductsApiApplication.class, args);
    }
}
```

**O que @SpringBootApplication faz?**
```java
@SpringBootApplication = 
    @Configuration +           // Classe de configuração
    @EnableAutoConfiguration + // Auto-config mágica
    @ComponentScan            // Escaneia @Component, @Service, etc
```

---

## Configuração (application.yml)

```yaml
# src/main/resources/application.yml
spring:
  application:
    name: products-api
  
  # H2 Database (para desenvolvimento)
  datasource:
    url: jdbc:h2:mem:testdb
    driverClassName: org.h2.Driver
    username: sa
    password:
  
  h2:
    console:
      enabled: true  # http://localhost:8080/h2-console
  
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
    hibernate:
      ddl-auto: create-drop  # Cria tabelas ao iniciar
    show-sql: true
    properties:
      hibernate:
        format_sql: true

server:
  port: 8080

logging:
  level:
    com.example.products: DEBUG
    org.springframework.web: INFO
```

---

## Rodando a aplicação

```bash
# Opção 1: Maven
./mvnw spring-boot:run

# Opção 2: Java (após build)
./mvnw clean package
java -jar target/products-api-0.0.1-SNAPSHOT.jar

# Opção 3: IDE
# Run ProductsApiApplication.java
```

**Output esperado:**
```
  .   ____          _            __ _ _
 /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
 \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
  '  |____| .__|_| |_|_| |_\__, | / / / /
 =========|_|==============|___/=/_/_/_/
 :: Spring Boot ::                (v3.2.0)

Started ProductsApiApplication in 2.1 seconds
Tomcat started on port(s): 8080 (http)
```
