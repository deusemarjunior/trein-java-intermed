# 07 — Employee API Production

Projeto de exercício do **Dia 7** — Dockerização, Observabilidade e Cloud Readiness.

## 🎯 Objetivo

Aplicar os conceitos de Docker, Docker Compose, Spring Actuator e logs estruturados
ao projeto Employee API, tornando-o **production-ready**.

---

## ✅ TODOs

| # | Arquivo | Descrição |
|---|---------|-----------|
| **TODO 1** | `Dockerfile` | Converter para multi-stage build (JDK → JRE) |
| **TODO 2** | `.dockerignore` | Adicionar exclusões (target, IDE, git, docs, OS) |
| **TODO 3** | `docker-compose.yml` | Adicionar services rabbitmq, redis, app + networks + volumes |
| **TODO 4** | `application.yml` | Configurar Actuator (endpoints, show-details) |
| **TODO 5** | `RabbitMQHealthIndicator.java` | Implementar custom HealthIndicator para RabbitMQ |
| **TODO 6a** | `logback-spring.xml` | Adicionar profile `prod` com LogstashEncoder JSON |
| **TODO 6b** | `MdcFilter.java` | Implementar filtro que injeta traceId/method/uri no MDC |
| **TODO 7** | `EmployeeService.java` | Adicionar logs contextuais com MDC em cada operação |

---

## 📋 Pré-requisitos

- Java 21+
- Maven 3.9+
- Docker e Docker Compose
- VS Code com extensões: Extension Pack for Java, Spring Boot Extension Pack, REST Client

---

## 🚀 Como executar

### Opção 1 — IDE (desenvolvimento)

```bash
# 1. Subir dependências
docker compose up -d postgres rabbitmq redis

# 2. Rodar a aplicação (VS Code → F5 ou terminal)
./mvnw spring-boot:run
```

### Opção 2 — Docker Compose completo (após TODO 3)

```bash
docker compose up --build -d
```

### Verificar

```
http://localhost:8092/api/employees
http://localhost:8092/actuator/health
```

---

## 🏗 Estrutura do Projeto

```
07-employee-api-production/
├── .vscode/
│   ├── launch.json
│   └── tasks.json
├── src/main/java/com/example/employeeapi/
│   ├── EmployeeApiProductionApplication.java
│   ├── config/
│   │   ├── CacheConfig.java
│   │   ├── MdcFilter.java              ← TODO 6b
│   │   └── RabbitMQConfig.java
│   ├── controller/
│   │   ├── DepartmentController.java
│   │   └── EmployeeController.java
│   ├── dto/
│   │   ├── EmployeeCreatedEvent.java
│   │   ├── EmployeeRequest.java
│   │   └── EmployeeResponse.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── ResourceNotFoundException.java
│   ├── health/
│   │   └── RabbitMQHealthIndicator.java ← TODO 5
│   ├── messaging/
│   │   ├── EmployeeEventPublisher.java
│   │   └── EmployeeNotificationConsumer.java
│   ├── model/
│   │   ├── Department.java
│   │   └── Employee.java
│   ├── repository/
│   │   ├── DepartmentRepository.java
│   │   └── EmployeeRepository.java
│   └── service/
│       ├── DepartmentService.java
│       └── EmployeeService.java         ← TODO 7
├── src/main/resources/
│   ├── application.yml                  ← TODO 4
│   ├── logback-spring.xml               ← TODO 6a
│   └── db/migration/
│       ├── V1__create_departments_table.sql
│       ├── V2__create_employees_table.sql
│       └── V3__seed_data.sql
├── Dockerfile                           ← TODO 1
├── .dockerignore                        ← TODO 2
├── docker-compose.yml                   ← TODO 3
├── api-requests.http
├── pom.xml
└── README.md
```

---

## 🐳 Portas utilizadas

| Serviço    | Porta Host | Porta Container |
|------------|-----------|----------------|
| App        | 8092      | 8092           |
| PostgreSQL | 5434      | 5432           |
| RabbitMQ   | 5674      | 5672           |
| RabbitMQ UI| 15674     | 15672          |
| Redis      | 6381      | 6379           |

---

## 📝 Dicas

1. Comece pelo **TODO 1** (Dockerfile) e vá em ordem
2. Use o projeto `07-docker-actuator-demo` como referência
3. Teste cada TODO individualmente antes de avançar
4. Use `docker compose logs -f app` para ver os logs estruturados
5. Acesse `http://localhost:8092/actuator/health` para validar os health checks
