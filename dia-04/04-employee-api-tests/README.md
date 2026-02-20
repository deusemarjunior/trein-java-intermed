# 04-employee-api-tests

Projeto exercício de **Testes** para a API de Gestão de Funcionários.

## Objetivo

Implementar testes unitários, parametrizados e de integração para uma API de funcionários completa (com DTOs, Service, Validation e Exception Handler).

## Tecnologias

| Tecnologia        | Versão      |
|--------------------|-------------|
| Java               | 21          |
| Spring Boot        | 3.2.2       |
| JUnit 5 (Jupiter)  | 5.10.x      |
| Mockito            | 5.x         |
| AssertJ            | 3.x         |
| Testcontainers     | 1.19.3      |
| PostgreSQL         | 16-alpine   |
| H2                 | runtime     |

## Pré-requisitos

- **Java 21**
- **Maven 3.9+**
- **Docker Desktop** rodando (para Testcontainers)

## Como executar

```bash
# Subir a aplicação (H2 em memória)
./mvnw spring-boot:run

# Executar TODOS os testes
./mvnw test

# Executar apenas testes unitários
./mvnw test -Dtest="*Test"

# Executar apenas testes de integração
./mvnw test -Dtest="*IT"
```

**Porta:** `8087`

## Estrutura do projeto

```text
src/
├── main/java/com/example/employee/
│   ├── EmployeeApiApplication.java
│   ├── controller/
│   │   └── EmployeeController.java
│   ├── dto/
│   │   ├── EmployeeRequest.java        ← com @Valid
│   │   └── EmployeeResponse.java
│   ├── exception/
│   │   ├── DepartmentNotFoundException.java
│   │   ├── DuplicateEmailException.java
│   │   ├── EmployeeNotFoundException.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── SalaryBelowMinimumException.java
│   ├── mapper/
│   │   └── EmployeeMapper.java
│   ├── model/
│   │   ├── Department.java
│   │   └── Employee.java
│   ├── repository/
│   │   ├── DepartmentRepository.java
│   │   └── EmployeeRepository.java
│   └── service/
│       └── EmployeeService.java        ← regras de negócio
├── main/resources/
│   ├── application.yml
│   └── data.sql
└── test/java/com/example/employee/
    ├── AbstractIntegrationTest.java     ← Base Testcontainers
    ├── EmployeeApiApplicationTests.java
    ├── builder/
    │   └── EmployeeBuilder.java         ← TODO 1
    ├── repository/
    │   └── EmployeeRepositoryIT.java    ← TODO 6, 7
    └── service/
        ├── EmployeeServiceParameterizedTest.java  ← TODO 5
        └── EmployeeServiceTest.java     ← TODO 2, 3, 4
```

## Exercícios (TODOs)

| #   | Arquivo                          | Descrição                                    |
|-----|-----------------------------------|----------------------------------------------|
| 1   | `EmployeeBuilder.java`           | Implementar Data Builder com valores padrão  |
| 2   | `EmployeeServiceTest.java`       | Teste: criar funcionário com sucesso         |
| 3   | `EmployeeServiceTest.java`       | Teste: salário abaixo do mínimo             |
| 4   | `EmployeeServiceTest.java`       | Teste: email duplicado                       |
| 5   | `EmployeeServiceParameterizedTest` | Teste parametrizado: CPFs válidos          |
| 6   | `EmployeeRepositoryIT.java`      | Teste integração: save/findById/findByEmail  |
| 7   | `EmployeeRepositoryIT.java`      | Teste integração: UNIQUE constraint email    |

### Ordem sugerida

1. **TODO 1** — EmployeeBuilder (base para todos os outros testes)
2. **TODO 2** — Teste de criação com sucesso (Mockito básico)
3. **TODO 3** — Teste de salário mínimo (regra de negócio)
4. **TODO 4** — Teste de email duplicado (verify + never)
5. **TODO 5** — Testes parametrizados (@CsvSource)
6. **TODO 6** — Testes de integração (Testcontainers)
7. **TODO 7** — Teste de UNIQUE constraint

## Regras de negócio implementadas

| Regra                    | Exceção                        | HTTP Status |
|--------------------------|--------------------------------|-------------|
| Salário ≥ R$ 1.412,00   | `SalaryBelowMinimumException` | 422         |
| Email único              | `DuplicateEmailException`     | 409         |
| Funcionário existe       | `EmployeeNotFoundException`   | 404         |
| Departamento existe      | `DepartmentNotFoundException` | 404         |
| Dados válidos (@Valid)   | `MethodArgumentNotValidException` | 400     |

## Endpoints da API

| Método | Endpoint              | Descrição              |
|--------|------------------------|------------------------|
| GET    | `/api/employees`      | Listar todos           |
| GET    | `/api/employees/{id}` | Buscar por ID          |
| POST   | `/api/employees`      | Criar funcionário      |
| PUT    | `/api/employees/{id}` | Atualizar funcionário  |
| DELETE | `/api/employees/{id}` | Deletar funcionário    |
