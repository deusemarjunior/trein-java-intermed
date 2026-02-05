# 04 - Layered Architecture Demo

**Status:** Projeto de exemplo simplificado

Este projeto demonstra uma arquitetura tradicional em 3 camadas.

## 🏛️ Camadas

### Presentation Layer (Apresentação)
- Controllers REST
- DTOs (Data Transfer Objects)

### Business Layer (Negócio)  
- Services com lógica de negócio
- Validações

### Data Access Layer (Dados)
- Repositories
- Entities JPA

## 📖 Estrutura Sugerida

```
presentation/
  ├── controller/
  │   └── ProductController.java
  └── dto/
      └── ProductDTO.java

business/
  ├── service/
  │   └── ProductService.java
  └── validator/
      └── ProductValidator.java

data/
  ├── repository/
  │   └── ProductRepository.java
  └── entity/
      └── ProductEntity.java
```

## 💡 Exercício

Os alunos podem criar este projeto seguindo a estrutura acima, implementando um CRUD simples de produtos usando as 3 camadas.

**Referência:** Consulte os projetos anteriores (dia-01 e dia-02) para exemplos de implementação com Spring Boot.
