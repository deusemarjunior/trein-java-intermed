# Products API - Spring Boot Demo

API REST completa para gerenciamento de produtos, demonstrando os conceitos do **Dia 1** do treinamento.

## 🎯 Recursos Demonstrados

### Java Moderno (17+)
- ✅ **Records** para DTOs (Request/Response)
- ✅ **Stream API** para transformações de dados
- ✅ **Optional** para evitar NullPointerException

### Spring Boot
- ✅ **REST Controllers** com `@RestController`
- ✅ **Spring Data JPA** com query methods
- ✅ **Bean Validation** com `@Valid`
- ✅ **Dependency Injection** via constructor
- ✅ **Exception Handling** com `@RestControllerAdvice`
- ✅ **Profiles** (dev/prod)
- ✅ **DevTools** para hot reload
- ✅ **H2 Database** em memória

## 🚀 Como Executar

### Pré-requisitos
- Java 17 ou superior
- Maven 3.8+

### Executar a aplicação

```bash
# Opção 1: Maven Wrapper (recomendado)
./mvnw spring-boot:run

# Opção 2: Maven instalado
mvn spring-boot:run

# Opção 3: Compilar e executar JAR
mvn clean package
java -jar target/products-api-1.0-SNAPSHOT.jar
```

A aplicação estará disponível em: **http://localhost:8080**

## 📋 Endpoints da API

### Listar todos os produtos
```bash
GET http://localhost:8080/api/products
```

### Listar produtos por categoria
```bash
GET http://localhost:8080/api/products?category=Electronics
```

### Buscar produto por ID
```bash
GET http://localhost:8080/api/products/1
```

### Criar produto
```bash
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Laptop Gaming",
  "description": "High-end gaming laptop",
  "price": 7500.00,
  "category": "Electronics"
}
```

### Atualizar produto
```bash
PUT http://localhost:8080/api/products/1
Content-Type: application/json

{
  "name": "Laptop Gaming Pro",
  "description": "Ultimate gaming laptop",
  "price": 8500.00,
  "category": "Electronics"
}
```

### Deletar produto
```bash
DELETE http://localhost:8080/api/products/1
```

## 🗄️ H2 Console

Acesse o console do banco de dados H2:

**URL:** http://localhost:8080/h2-console

**Configurações:**
- JDBC URL: `jdbc:h2:mem:testdb`
- User: `sa`
- Password: *(deixar vazio)*

## 🔧 Profiles

### Desenvolvimento (padrão)
```bash
./mvnw spring-boot:run
# ou
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Produção
```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```

## 📁 Estrutura do Projeto

```
src/main/java/com/example/products/
├── ProductsApiApplication.java       # Classe principal
├── config/
│   └── AppConfig.java               # Configurações e dados de teste
├── controller/
│   └── ProductController.java       # REST endpoints
├── service/
│   └── ProductService.java          # Lógica de negócio
├── repository/
│   └── ProductRepository.java       # Acesso a dados
├── model/
│   └── Product.java                 # Entidade JPA
├── dto/
│   ├── request/
│   │   ├── CreateProductRequest.java
│   │   └── UpdateProductRequest.java
│   └── response/
│       └── ProductResponse.java
└── exception/
    ├── ProductNotFoundException.java
    └── GlobalExceptionHandler.java
```

## 🧪 Testando com cURL

```bash
# Listar todos
curl http://localhost:8080/api/products

# Criar produto
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Mouse","description":"Gaming mouse","price":150.00,"category":"Electronics"}'

# Buscar por ID
curl http://localhost:8080/api/products/1

# Atualizar
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Mouse Pro","description":"Pro gaming mouse","price":200.00,"category":"Electronics"}'

# Deletar
curl -X DELETE http://localhost:8080/api/products/1
```

## 💡 Conceitos Importantes

### 1. Camadas da Aplicação
```
Controller → Service → Repository → Database
    ↓          ↓           ↓
  DTOs    Business    Entity/Model
          Logic
```

### 2. Records vs Classes
- **Records**: DTOs imutáveis (Request/Response)
- **Classes**: Entidades JPA (precisam setters)

### 3. Validações
```java
@NotBlank(message = "Name is required")
@Size(min = 3, max = 100)
@DecimalMin(value = "0.01")
```

### 4. Repository Query Methods
Spring cria queries automaticamente pelo nome:
```java
findByCategory(String category)
findByNameContainingIgnoreCase(String name)
findByPriceGreaterThan(BigDecimal price)
```

### 5. Exception Handling
`@RestControllerAdvice` trata exceções globalmente

## 📚 Próximos Passos

- [ ] Adicionar testes unitários
- [ ] Implementar paginação
- [ ] Adicionar Swagger/OpenAPI
- [ ] Conectar a banco real (PostgreSQL)
- [ ] Adicionar autenticação

## 🎓 Material de Estudo

Este projeto demonstra os conceitos do **Dia 1** do treinamento:
- Java Moderno (Records, Stream API, Optional)
- Spring Boot Fundamentals
- REST API completa
- Bean Validation
- Profiles
- DevTools
