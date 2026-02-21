# 05-employee-api-secure

Projeto de **exercício** do Dia 05 — Comunicação entre Sistemas e Segurança.

## Objetivo

Aplicar na prática os conceitos de Feign Client, Resilience4j, CORS, Spring Security + JWT e OpenAPI/Swagger em uma API de gestão de funcionários.

## Porta

```
http://localhost:8089
```

## TODOs

| # | Descrição | Arquivo | Slide |
|---|---|---|---|
| 1 | Criar `@FeignClient` para departamentos externos | `client/ExternalDepartmentClient.java` | 09 |
| 2 | Implementar `ErrorDecoder` customizado | `client/CustomErrorDecoder.java` | 09 |
| 3 | Configurar Resilience4j (retry + circuit breaker + fallback) | `service/DepartmentIntegrationService.java` + `application.yml` | 09 |
| 4 | Configurar CORS | `config/CorsConfig.java` | 09 |
| 5 | Implementar `AuthController` (POST /auth/login) | `controller/AuthController.java` | 10 |
| 6 | Configurar `SecurityFilterChain` | `config/SecurityConfig.java` | 10 |
| 7 | Implementar `JwtAuthenticationFilter` | `security/JwtAuthenticationFilter.java` | 10 |
| 8 | Adicionar annotations Swagger nos endpoints e DTOs | Controllers + DTOs | 10 |

## O que já está pronto

- Entidades: `Employee`, `Department`, `User`
- Repositórios: `EmployeeRepository`, `DepartmentRepository`, `UserRepository`
- DTOs: `EmployeeRequest`, `EmployeeResponse`, `LoginRequest`, `TokenResponse`, `ExternalDepartmentResponse`
- Mapper: `EmployeeMapper`
- Exceptions: todas as exceções + `GlobalExceptionHandler`
- Service: `EmployeeService`, `UserService` (completos)
- Security: `JwtUtil` (completo)
- Config: `SwaggerConfig`, `FeignConfig` (completos)
- Controller: `EmployeeController`, `ExternalDepartmentController` (funcionais, faltam annotations)
- Dados: `data.sql` com departamentos, usuários e funcionários

## Usuários de Teste (data.sql)

| Email | Senha | Role |
|---|---|---|
| admin@email.com | admin123 | ADMIN |
| user@email.com | user123 | USER |

## Como Executar

```bash
# Via Maven
mvn spring-boot:run

# Via VS Code
# Use a task "mvn: spring-boot:run (8089)" ou o Launch Configuration
```

## Swagger UI (após TODO 8)

```
http://localhost:8089/swagger-ui.html
```

## H2 Console

```
http://localhost:8089/h2-console
JDBC URL: jdbc:h2:mem:employeedb
User: sa
Password: (vazio)
```

## Estrutura

```
src/main/java/com/example/employee/
├── EmployeeApiSecureApplication.java
├── client/
│   ├── CustomErrorDecoder.java          ← TODO 2
│   └── ExternalDepartmentClient.java    ← TODO 1
├── config/
│   ├── CorsConfig.java                  ← TODO 4
│   ├── FeignConfig.java
│   ├── SecurityConfig.java              ← TODO 6
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthController.java              ← TODO 5
│   ├── EmployeeController.java          ← TODO 8
│   └── ExternalDepartmentController.java ← TODO 8
├── dto/
│   ├── EmployeeRequest.java             ← TODO 8
│   ├── EmployeeResponse.java            ← TODO 8
│   ├── ExternalDepartmentResponse.java
│   ├── LoginRequest.java
│   └── TokenResponse.java
├── exception/
│   ├── DepartmentNotFoundException.java
│   ├── DuplicateEmailException.java
│   ├── EmployeeNotFoundException.java
│   ├── ExternalServiceException.java
│   ├── GlobalExceptionHandler.java
│   └── InvalidCredentialsException.java
├── mapper/
│   └── EmployeeMapper.java
├── model/
│   ├── Department.java
│   ├── Employee.java
│   └── User.java
├── repository/
│   ├── DepartmentRepository.java
│   ├── EmployeeRepository.java
│   └── UserRepository.java
├── security/
│   ├── JwtAuthenticationFilter.java     ← TODO 7
│   └── JwtUtil.java
└── service/
    ├── DepartmentIntegrationService.java ← TODO 3
    ├── EmployeeService.java
    └── UserService.java
```

## Dicas

1. **Comece pelos TODOs 1-4** (Feign, Resilience4j, CORS) — são independentes da segurança
2. **Depois TODOs 5-7** (Auth, Security, JWT Filter) — dependem um do outro
3. **Por último TODO 8** (Swagger) — pode ser feito a qualquer momento
4. O projeto já roda sem os TODOs (com segurança desabilitada)
5. Consulte o projeto demo (`05-integration-security-demo`) como referência
