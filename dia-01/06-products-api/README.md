# Products API - Servlet + JDBC

API REST para gerenciamento de produtos usando **Servlet puro** e **JDBC** (sem Spring Boot).

## 🎯 Objetivo

Demonstrar como criar uma API REST completa sem frameworks, usando apenas:
- **Jakarta Servlet** para endpoints HTTP
- **JDBC** para acesso ao banco de dados
- **Gson** para serialização JSON
- **Tomcat Embedded** como servidor
- **H2 Database** como banco em memória

## 🏗️ Arquitetura

```
Cliente (Postman/cURL)
    │
    ▼
ProductServlet (HTTP → JSON)
    │
    ▼
ProductDAO (JDBC → SQL)
    │
    ▼
H2 Database (em memória)
```

## 📁 Estrutura

```
src/main/java/com/example/products/
├── ProductsApp.java           # Main - Tomcat Embedded
├── config/
│   └── DatabaseConfig.java    # Configuração JDBC e DDL
├── model/
│   └── Product.java           # Modelo de dados (POJO)
├── dto/
│   ├── CreateProductRequest.java  # DTO de entrada (Record)
│   └── ProductResponse.java      # DTO de saída (Record)
├── dao/
│   └── ProductDAO.java        # Data Access Object (JDBC)
└── servlet/
    ├── ProductServlet.java    # REST endpoints
    └── LocalDateTimeAdapter.java # Gson adapter
```

## 🚀 Como Executar

```bash
# Compilar e executar
mvn clean compile exec:java -Dexec.mainClass="com.example.products.ProductsApp"
```

## 📡 Endpoints

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | `/api/products` | Listar todos os produtos |
| GET | `/api/products/{id}` | Buscar por ID |
| GET | `/api/products?category=X` | Filtrar por categoria |
| GET | `/api/products?name=X` | Buscar por nome |
| POST | `/api/products` | Criar novo produto |
| PUT | `/api/products/{id}` | Atualizar produto |
| DELETE | `/api/products/{id}` | Deletar produto |

## 🧪 Testando

Use o arquivo `api-requests.http` com a extensão REST Client do VS Code, ou teste com cURL:

```bash
# Listar produtos (já vem com dados de exemplo!)
curl http://localhost:8080/api/products

# Criar produto
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Headset","description":"Headset 7.1","price":350,"category":"Electronics"}'

# Buscar por ID
curl http://localhost:8080/api/products/1

# Filtrar por categoria
curl "http://localhost:8080/api/products?category=Electronics"

# Atualizar
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Laptop Pro","description":"Updated","price":9000,"category":"Electronics"}'

# Deletar
curl -X DELETE http://localhost:8080/api/products/1
```

## 🔑 Conceitos Demonstrados

| Conceito | Implementação |
|----------|--------------|
| HTTP Servlet | `ProductServlet extends HttpServlet` |
| REST API | doGet, doPost, doPut, doDelete |
| JDBC | PreparedStatement, ResultSet |
| DAO Pattern | `ProductDAO` encapsula acesso a dados |
| DTOs com Records | `CreateProductRequest`, `ProductResponse` |
| Validação manual | Construtor compacto do Record |
| JSON | Gson para serialização/deserialização |
| Servidor embedded | Tomcat Embedded |
| Banco em memória | H2 Database |

## 🔄 Comparação com Spring Boot (Dia 2)

| Aspecto | Este projeto (Servlet+JDBC) | Spring Boot (Dia 2) |
|---------|--------------------------|-------------------|
| Servidor | Tomcat manual | Auto-configurado |
| Routing | `pathInfo` manual | `@GetMapping` |
| JSON | Gson manual | Jackson automático |
| Banco | JDBC + PreparedStatement | JPA/Hibernate |
| Validação | Manual no construtor | `@Valid` + annotations |
| Injeção | `new ProductDAO()` | `@Autowired` |
| Config | Código Java | `application.yml` |
| Boilerplate | Alto | Baixo |

## 📚 Slides Relacionados

- **Slide 9:** Fundamentos Web & Servlets
- **Slide 10:** Configurando o Projeto
- **Slide 11:** Modelo, DAO e DTOs
- **Slide 12:** Criando o Servlet REST
- **Slide 13:** Testando a API
