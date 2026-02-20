# 04-testing-demo

Projeto demonstração de **Estratégias de Testes** com Spring Boot.

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

**Porta:** `8086`

## Estrutura do projeto

```text
src/
├── main/java/com/example/testingdemo/
│   ├── TestingDemoApplication.java
│   ├── controller/
│   │   └── ProductController.java
│   ├── dto/
│   │   ├── ProductRequest.java
│   │   └── ProductResponse.java
│   ├── exception/
│   │   ├── DuplicateSkuException.java
│   │   ├── GlobalExceptionHandler.java
│   │   └── ProductNotFoundException.java
│   ├── mapper/
│   │   └── ProductMapper.java
│   ├── model/
│   │   └── Product.java
│   ├── repository/
│   │   └── ProductRepository.java
│   └── service/
│       └── ProductService.java
├── main/resources/
│   ├── application.yml
│   └── data.sql
└── test/java/com/example/testingdemo/
    ├── AbstractIntegrationTest.java         ← Base Testcontainers
    ├── TestingDemoApplicationTests.java
    ├── builder/
    │   └── ProductBuilder.java              ← Data Builder
    ├── repository/
    │   └── ProductRepositoryIT.java         ← Integração c/ PostgreSQL
    └── service/
        ├── ProductServiceParameterizedTest.java  ← @ParameterizedTest
        └── ProductServiceTest.java          ← Unitários c/ Mockito
```

## Tipos de testes demonstrados

| Tipo             | Classe                           | Ferramenta         |
|------------------|-----------------------------------|--------------------|
| Unitário         | `ProductServiceTest`             | Mockito + AssertJ  |
| Parametrizado    | `ProductServiceParameterizedTest`| JUnit 5 @CsvSource |
| Integração       | `ProductRepositoryIT`            | Testcontainers     |
| Data Builder     | `ProductBuilder`                 | Padrão Builder     |

## Endpoints da API

| Método | Endpoint                        | Descrição                    |
|--------|----------------------------------|------------------------------|
| GET    | `/api/products`                 | Listar todos                 |
| GET    | `/api/products/{id}`            | Buscar por ID                |
| GET    | `/api/products/sku/{sku}`       | Buscar por SKU               |
| POST   | `/api/products`                 | Criar produto                |
| PUT    | `/api/products/{id}`            | Atualizar produto            |
| DELETE | `/api/products/{id}`            | Deletar produto              |
| GET    | `/api/products/search?name=...` | Buscar por nome              |
| GET    | `/api/products/price-range`     | Buscar por faixa de preço    |
| GET    | `/api/products/in-stock`        | Listar em estoque (paginado) |
