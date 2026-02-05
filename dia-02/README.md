# Dia 2 - Spring Framework Core: Persistência e APIs

**Duração**: 5 horas  
**Objetivo**: Dominar Spring Web avançado e Spring Data JPA para persistência de dados

---

## 🎯 Agenda do Dia

| Horário | Duração | Tópico | Tipo |
|---------|---------|--------|------|
| 09:00 - 09:15 | 15min | Review Dia 1 & Setup | Discussão |
| 09:15 - 10:15 | 1h | Spring Web Avançado | Teórico + Demo |
| 10:15 - 10:30 | 15min | ☕ Coffee Break | - |
| 10:30 - 12:00 | 1h30 | Spring Data JPA & Entities | Teórico + Demo |
| 12:00 - 13:00 | 1h | 🍽️ Almoço | - |
| 13:00 - 14:00 | 1h | Repositories & Queries | Teórico + Demo |
| 14:00 - 15:00 | 1h | DTOs & Exception Handling | Teórico + Demo |
| 15:00 - 15:15 | 15min | ☕ Coffee Break | - |
| 15:15 - 16:15 | 1h | Exercício Prático Completo | Hands-on |
| 16:15 - 16:30 | 15min | Review e Q&A | Discussão |

---

## 📦 Material Necessário (Checklist Instrutor)

### Software
- [ ] PostgreSQL ou Docker com Postgres rodando
- [ ] DBeaver ou pgAdmin (cliente SQL)
- [ ] Postman/Insomnia com collections prontas
- [ ] H2 Console configurado

### Preparação
- [ ] Projeto Spring Boot do Dia 1 funcionando
- [ ] Scripts SQL de exemplo
- [ ] Diagrama ER do modelo de dados
- [ ] Exemplos de DTOs prontos

---

## 📋 Conteúdo Programático

### Manhã (3 horas)

#### 1. Spring Web Avançado (1.5h)
- **Controllers e RestControllers**
  - Diferença entre @Controller e @RestController
  - Request Mapping avançado
  - Consuming e Producing (JSON, XML)
  
- **Request/Response Handling**
  - @PathVariable, @RequestParam, @RequestHeader
  - @RequestBody e validação com Bean Validation
  - ResponseEntity e HTTP Status Codes
  - Exception Handling (@ExceptionHandler, @ControllerAdvice)
  
- **Content Negotiation**
  - JSON (Jackson)
  - XML (JAXB)
  - Custom converters

#### 2. Spring Data JPA (1.5h)
- **Configuração JPA**
  - Dependências necessárias
  - application.yml: datasource, jpa, hibernate
  - Dialetos de banco de dados
  
- **Entities e Mapeamento**
  - @Entity, @Table, @Id, @GeneratedValue
  - Tipos de dados e conversões
  - @Column: nullable, unique, length
  - @Temporal, @Enumerated, @Lob
  
- **Relacionamentos**
  - @OneToOne
  - @OneToMany e @ManyToOne
  - @ManyToMany
  - Cascade e Fetch Types (LAZY vs EAGER)
  - Bidirecionalidade

### Tarde (2 horas)

#### 3. Spring Data Repositories (1h)
- **JpaRepository**
  - CRUD operations
  - Métodos derivados (findBy, existsBy, deleteBy)
  - Query Methods
  
- **Queries Customizadas**
  - @Query com JPQL
  - @Query com SQL nativo
  - @Param e parâmetros nomeados
  - Paginação e Ordenação (Pageable, Sort)
  
- **Specifications e Criteria API**
  - Queries dinâmicas
  - Filtros complexos

#### 4. DTOs e Mapeamento (1h)
- **Por que usar DTOs**
  - Separação de concerns
  - Controle de exposição de dados
  - Versionamento de API
  
- **Padrões de conversão**
  - Manual (construtores, builders)
  - MapStruct
  - ModelMapper
  
- **Boas práticas**
  - Request DTOs vs Response DTOs
  - Validação em DTOs
  - Documentação com @Schema (OpenAPI)

## 💻 Exercícios Práticos

### Exercício 1: Entidades e Relacionamentos (1h)

Crie um modelo de dados para um **Blog**:

```
Post (id, title, content, author, createdAt, updatedAt)
Comment (id, text, author, createdAt, postId)
Tag (id, name)
Post_Tag (relacionamento N:N)
```

**Requisitos**:
- Um Post pode ter vários Comments
- Um Post pode ter várias Tags
- Uma Tag pode estar em vários Posts
- Use fetch LAZY apropriadamente
- Configure cascade operations

### Exercício 2: API com Persistência (1.5h)

Estenda a API de Tarefas do Dia 1 para usar banco de dados:

**Endpoints**:
```
GET    /api/tasks?page=0&size=10&sort=createdAt,desc
GET    /api/tasks/search?keyword=java&completed=true
GET    /api/tasks/{id}
POST   /api/tasks
PUT    /api/tasks/{id}
PATCH  /api/tasks/{id}/complete
DELETE /api/tasks/{id}
```

**Requisitos**:
- Use PostgreSQL/H2
- DTOs para Request e Response
- Validação (@NotBlank, @NotNull, etc)
- Paginação e ordenação
- Tratamento de erros (404, 400, 500)
- Exception handling global

**Task Entity**:
```java
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank
    @Column(nullable = false)
    private String title;
    
    private String description;
    
    private boolean completed;
    
    @Enumerated(EnumType.STRING)
    private Priority priority;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
```

## 📚 Material de Estudo

### Leitura Obrigatória
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [JPA Relationships](https://www.baeldung.com/jpa-hibernate-associations)
- [Bean Validation](https://www.baeldung.com/javax-validation)

### Leitura Complementar
- [MapStruct Guide](https://mapstruct.org/)
- [Exception Handling in Spring Boot](https://www.baeldung.com/exception-handling-for-rest-with-spring)
- [Pagination and Sorting](https://www.baeldung.com/spring-data-jpa-pagination-sorting)

### Exemplos de Código
```java
// Repository customizado
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    @Query("SELECT t FROM Task t WHERE t.completed = :completed")
    Page<Task> findByCompleted(@Param("completed") boolean completed, Pageable pageable);
    
    List<Task> findByTitleContainingIgnoreCase(String keyword);
    
    @Query("SELECT t FROM Task t WHERE t.priority = :priority AND t.completed = false")
    List<Task> findPendingByPriority(@Param("priority") Priority priority);
}

// DTO com validação
public record CreateTaskRequest(
    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100)
    String title,
    
    @Size(max = 500)
    String description,
    
    @NotNull
    Priority priority
) {}

// Exception Handler Global
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(new ErrorResponse(ex.getMessage()));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        // Extrair erros de validação
        return ResponseEntity.badRequest()
            .body(new ErrorResponse("Validation failed"));
    }
}
```

## 🎯 Objetivos de Aprendizagem

Ao final deste dia, você deve ser capaz de:

- ✅ Criar entidades JPA com relacionamentos complexos
- ✅ Desenvolver repositories customizados
- ✅ Implementar paginação e ordenação
- ✅ Usar DTOs para separar camadas
- ✅ Validar dados de entrada
- ✅ Tratar exceções de forma global e consistente

## 🏠 Tarefa de Casa

1. **Expandir o Blog**:
   - Adicionar Category (relacionamento com Post)
   - Implementar busca por tags
   - Endpoint para posts mais comentados
   - Soft delete para Posts

2. **Estudar**:
   - N+1 problem e como evitar
   - Diferença entre save() e saveAndFlush()
   - Transações (@Transactional)

3. **Preparação para Dia 3**:
   - Ler sobre princípios SOLID
   - Conhecer Design Patterns básicos (Factory, Strategy)

## 📝 Notas do Instrutor

```
Pontos de atenção:
- Demonstrar problema N+1 com fetch LAZY
- Mostrar como usar @EntityGraph
- Explicar quando usar EAGER vs LAZY
- Enfatizar importância de DTOs
- Mostrar como debugar queries do Hibernate (show-sql)
- Demonstrar uso do H2 Console
```

## 🔗 Links Úteis

- [H2 Database](https://www.h2database.com/)
- [PostgreSQL Download](https://www.postgresql.org/download/)
- [DB Diagram Tool](https://dbdiagram.io/)
- [JPA Buddy Plugin](https://plugins.jetbrains.com/plugin/15075-jpa-buddy)
