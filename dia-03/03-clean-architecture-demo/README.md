# 03-clean-architecture-demo

API de Catálogo de Produtos demonstrando **Clean Architecture** (Hexagonal), **Clean Code**, **Problem Details** e **Bean Validation**.

## 🎯 Conceitos Demonstrados

- Arquitetura Hexagonal (Ports & Adapters)
- DTOs (Request/Response) com Records
- Mapeamento Entity ↔ DTO
- `@ControllerAdvice` + Problem Details (RFC 7807)
- Custom Exceptions (`ProductNotFoundException`, `DuplicateSkuException`)
- Bean Validation (`@Valid`, `@NotBlank`, `@Positive`)
- Custom Validator (`@ValidSku`)

## 🚀 Como Rodar

```bash
mvn spring-boot:run
# Porta: 8083
# H2 Console: http://localhost:8083/h2-console
#   JDBC URL: jdbc:h2:mem:productsdb
#   User: sa / Password: (vazio)
```

## 📁 Estrutura de Pacotes

```
com.example.cleanarchitecture/
├── domain/
│   ├── model/Product.java
│   ├── port/in/ProductUseCase.java
│   ├── port/out/ProductRepositoryPort.java
│   ├── service/ProductService.java
│   └── exception/
│       ├── ProductNotFoundException.java
│       └── DuplicateSkuException.java
├── adapter/
│   ├── in/web/
│   │   ├── ProductController.java
│   │   ├── dto/ProductRequest.java
│   │   ├── dto/ProductResponse.java
│   │   ├── mapper/ProductWebMapper.java
│   │   └── handler/GlobalExceptionHandler.java
│   └── out/persistence/
│       ├── JpaProductRepository.java
│       ├── ProductJpaEntity.java
│       ├── ProductPersistenceMapper.java
│       └── SpringDataProductRepository.java
├── config/BeanConfig.java
└── validation/
    ├── ValidSku.java
    └── SkuValidator.java
```

## 🔗 Endpoints

| Método | URL | Descrição |
|--------|-----|-----------|
| GET | /api/products | Listar todos |
| GET | /api/products/{id} | Buscar por ID |
| POST | /api/products | Criar produto |
| PUT | /api/products/{id} | Atualizar produto |
| DELETE | /api/products/{id} | Deletar produto |

## 📝 Testar

Use o arquivo `api-requests.http` com VS Code REST Client ou Postman.
