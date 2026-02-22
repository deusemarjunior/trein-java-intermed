# 06-employee-api-advanced

Exercício prático do **Dia 6** — Persistência Avançada e Mensageria.

## Objetivo

Aplicar na API de funcionários os conceitos aprendidos no dia:

1. **Identificar e corrigir o problema N+1** com JOIN FETCH
2. **Criar projeção DTO** com EmployeeSummary + Pageable
3. **Configurar Flyway** com migrations SQL versionadas
4. **Implementar RabbitMQ** para notificações de novos funcionários
5. **Adicionar Redis Cache** na listagem de departamentos

## TODOs

| # | O que fazer | Arquivo(s) |
|:-:|-------------|------------|
| 1 | Identificar N+1 | Execute `GET /api/employees` e conte queries nos logs |
| 2 | Corrigir N+1 com JOIN FETCH | `EmployeeRepository.java` + `EmployeeService.java` |
| 3 | Criar projeção DTO | `EmployeeSummary.java` + `EmployeeRepository.java` |
| 4 | Paginação com Pageable | `EmployeeService.java` + `EmployeeController.java` |
| 5 | Flyway migrations | `V1__`, `V2__`, `V3__` em `db/migration/` + `application.yml` |
| 6 | RabbitMQ producer | `RabbitMQConfig.java` + `EmployeeEventPublisher.java` + `EmployeeService.java` |
| 7 | RabbitMQ consumer | `EmployeeNotificationConsumer.java` |
| 8 | Redis cache | `EmployeeApiAdvancedApplication.java` + `DepartmentService.java` + `application.yml` |

## Pré-requisitos

- Java 21
- Maven 3.9+
- Docker Desktop

## Como executar

### 1. Subir infraestrutura

```bash
docker compose up -d
```

Inicia:
- **PostgreSQL** em `localhost:5433` (employeedb / employee / employee123)
- **RabbitMQ** em `localhost:5673` (management: http://localhost:15673 — guest/guest)
- **Redis** em `localhost:6380`

> **Nota:** As portas são diferentes do projeto demo para evitar conflitos.

### 2. Rodar a aplicação

```bash
mvn spring-boot:run
```

A API sobe na porta **8091**.

### 3. Testar

Use o arquivo `api-requests.http` ou curl.

## Estrutura do projeto

```
src/main/java/com/example/employee/
├── EmployeeApiAdvancedApplication.java   ← TODO 8: @EnableCaching
├── config/
│   ├── CacheConfig.java                  ← Redis config (pronto)
│   └── RabbitMQConfig.java               ← TODO 6: Exchange, Queue, Binding
├── controller/
│   ├── DepartmentController.java         ← TODO 8: verificar cache
│   └── EmployeeController.java           ← TODO 1, 4
├── dto/
│   ├── EmployeeCreatedEvent.java         ← Evento RabbitMQ (pronto)
│   ├── EmployeeRequest.java              ← DTO entrada (pronto)
│   ├── EmployeeResponse.java             ← DTO saída (pronto)
│   └── EmployeeSummary.java              ← TODO 3: projeção DTO
├── exception/
│   ├── GlobalExceptionHandler.java
│   └── ResourceNotFoundException.java
├── messaging/
│   ├── EmployeeEventPublisher.java       ← TODO 6: publisher
│   └── EmployeeNotificationConsumer.java ← TODO 7: consumer
├── model/
│   ├── Department.java
│   └── Employee.java                     ← TODO 1: observe N+1
├── repository/
│   ├── DepartmentRepository.java
│   └── EmployeeRepository.java           ← TODO 2, 3: JOIN FETCH + projeção
└── service/
    ├── DepartmentService.java            ← TODO 8: @Cacheable
    └── EmployeeService.java              ← TODO 2, 4, 6: N+1 fix + paginação + evento
```

## Verificações

### N+1 (TODOs 1-2)
- Antes: `GET /api/employees` → logs mostram 1 + N queries
- Depois: `GET /api/employees` → logs mostram 1 query com JOIN

### Projeção + Paginação (TODOs 3-4)
- `GET /api/employees/summary?page=0&size=5` → retorna apenas id, name, departmentName

### Flyway (TODO 5)
- Migrations executadas ao subir a aplicação
- Tabela `flyway_schema_history` criada no banco

### RabbitMQ (TODOs 6-7)
- `POST /api/employees` → log "📤 Publicando evento" + log "📧 Email de boas-vindas"
- Management UI: http://localhost:15673

### Redis Cache (TODO 8)
- 1ª chamada `GET /api/departments/1` → log "CACHE MISS"
- 2ª chamada `GET /api/departments/1` → sem log (veio do cache!)

```bash
docker exec -it employee-redis redis-cli
KEYS *
```

## Parar infraestrutura

```bash
docker compose down
```
