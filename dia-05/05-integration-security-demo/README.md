# 05-integration-security-demo

Projeto de **demonstração** do Dia 05 — Comunicação entre Sistemas e Segurança.

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Spring Boot 3.2.2 | Framework principal |
| Spring Cloud OpenFeign | Cliente declarativo REST |
| Resilience4j 2.2.0 | Retry, Circuit Breaker, Fallback |
| Spring Security | Autenticação e autorização |
| JJWT 0.11.5 | Geração e validação de tokens JWT |
| SpringDoc OpenAPI 2.3.0 | Swagger UI |
| H2 Database | Banco em memória para dev |

## Porta

```
http://localhost:8088
```

## Endpoints Principais

| Método | Path | Auth | Descrição |
|---|---|---|---|
| `POST` | `/auth/login` | — | Login (retorna JWT) |
| `GET` | `/api/products` | — | Listar produtos |
| `GET` | `/api/products/{id}` | — | Buscar produto |
| `POST` | `/api/products` | ADMIN | Criar produto |
| `PUT` | `/api/products/{id}` | ADMIN | Atualizar produto |
| `DELETE` | `/api/products/{id}` | ADMIN | Remover produto |
| `GET` | `/api/departments` | USER/ADMIN | Listar departamentos (Feign) |
| `GET` | `/api/departments/{id}` | USER/ADMIN | Buscar departamento (Feign) |

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
# Use a task "mvn: spring-boot:run (8088)" ou o Launch Configuration
```

## Swagger UI

Após iniciar a aplicação:

```
http://localhost:8088/swagger-ui.html
```

1. Clique em **Authorize** (cadeado)
2. Cole o token obtido em `POST /auth/login`
3. Teste os endpoints protegidos

## H2 Console

```
http://localhost:8088/h2-console
JDBC URL: jdbc:h2:mem:demodb
User: sa
Password: (vazio)
```

## Estrutura

```
src/main/java/com/example/demo/
├── IntegrationSecurityDemoApplication.java
├── client/
│   ├── CustomErrorDecoder.java
│   └── DepartmentClient.java
├── config/
│   ├── CorsConfig.java
│   ├── FeignConfig.java
│   ├── SecurityConfig.java
│   └── SwaggerConfig.java
├── controller/
│   ├── AuthController.java
│   ├── DepartmentController.java
│   └── ProductController.java
├── dto/
│   ├── DepartmentResponse.java
│   ├── LoginRequest.java
│   ├── ProductRequest.java
│   ├── ProductResponse.java
│   └── TokenResponse.java
├── exception/
│   ├── DepartmentNotFoundException.java
│   ├── DuplicateSkuException.java
│   ├── ExternalServiceException.java
│   ├── GlobalExceptionHandler.java
│   ├── InvalidCredentialsException.java
│   └── ProductNotFoundException.java
├── mapper/
│   └── ProductMapper.java
├── model/
│   ├── Product.java
│   └── User.java
├── repository/
│   ├── ProductRepository.java
│   └── UserRepository.java
├── security/
│   ├── JwtAuthenticationFilter.java
│   └── JwtUtil.java
└── service/
    ├── DepartmentIntegrationService.java
    ├── ProductService.java
    └── UserService.java
```

## Conceitos Demonstrados

- **Feign Client**: Cliente declarativo para comunicação entre serviços
- **Resilience4j**: Retry automático e Circuit Breaker com fallback
- **Spring Security + JWT**: Autenticação stateless com tokens
- **CORS**: Configuração para permitir acesso de origens diferentes
- **OpenAPI/Swagger**: Documentação interativa da API
- **@PreAuthorize**: Autorização baseada em roles (ADMIN/USER)
- **ProblemDetail (RFC 7807)**: Respostas de erro padronizadas
