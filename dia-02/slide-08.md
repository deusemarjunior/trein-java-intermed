# Slide 8: Review Spring Boot Basics & Setup JPA

**Horário:** 11:15 - 11:30

---

## ✅ O que vimos até agora (Spring Boot Basics)

### Spring Boot
- ✓ IoC e DI (Inversion of Control, Dependency Injection)
- ✓ Auto-configuração
- ✓ Starters
- ✓ Profiles (dev, test, prod)
- ✓ DevTools (hot reload)

### Primeira API REST com Spring Boot
- ✓ Controller (endpoints com @GetMapping, @PostMapping, etc)
- ✓ Service (lógica de negócio com @Service)
- ✓ Repository (Spring Data JPA - CRUD automático)
- ✓ Entity (modelo JPA com @Entity)
- ✓ DTOs com Records (Request/Response)
- ✓ Validação (@Valid + @NotBlank, @Size, etc)

### Servlet+JDBC (Dia 1) vs Spring Boot (Dia 2)

| Dia 1 | Dia 2 |
|-------|-------|
| `new ProductDAO()` | `@Autowired` / Injeção |
| `PreparedStatement` | `JpaRepository` |
| `mapRow(ResultSet)` | Mapeamento automático |
| `response.setStatus(201)` | `ResponseEntity.status(CREATED)` |
| Validação manual | `@Valid` + Bean Validation |

---

## 🤔 Perguntas Comuns

**Q: @Autowired é obrigatório?**  
A: Não! Constructor injection não precisa (recomendado). Field/Setter injection precisam.

**Q: DDL-auto create-drop é seguro?**  
A: NUNCA em produção! Só dev/test. Use `validate` em prod.

**Q: Como debugar aplicação Spring?**  
A: Logs, breakpoints, Spring Boot Actuator (mais adiante no curso).

---

## 🎯 Próximos passos (tarde)

Agora vamos aprofundar:
- HTTP & REST avançado (status codes, ResponseEntity)
- Request/Response handling (@PathVariable, @RequestParam, @RequestBody)
- Exception Handling global (@ControllerAdvice)
- JPA Entities e Relacionamentos
- Spring Data JPA Repositories
- Query Methods e JPQL
- Paginação e Ordenação
- DTOs e Mapeamento

---

## 🔧 Setup para Persistência

### PostgreSQL com Docker

```bash
docker run --name postgres-dev \
  -e POSTGRES_PASSWORD=postgres \
  -e POSTGRES_DB=java_training \
  -p 5432:5432 \
  -d postgres:15
```

### Dependências (pom.xml)

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <scope>runtime</scope>
</dependency>
```
