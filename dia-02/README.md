# Dia 2 - Spring Boot, Spring Data JPA e APIs REST

**Duração**: 8 horas (dia completo)  
**Objetivo**: Dominar Spring Boot fundamentals, Spring Web avançado e Spring Data JPA para persistência

---

## 🎯 Agenda do Dia

| Horário | Duração | Tópico | Tipo |
|---------|---------|--------|------|
| 09:00 - 09:15 | 15min | Fundamentos Web & Spring Boot | Teórico |
| 09:15 - 09:30 | 15min | Criando Primeiro Projeto Spring Boot | Demo |
| 09:30 - 09:45 | 15min | Estrutura do Projeto Spring Boot | Teórico |
| 09:45 - 10:30 | 45min | Primeira API REST com Spring Boot | Hands-on |
| 10:30 - 11:00 | 30min | Testando a API + Profiles + DevTools | Demo |
| 11:00 - 11:15 | 15min | ☕ Coffee Break | - |
| 11:15 - 11:30 | 15min | Review Spring Boot & Setup Persistência | Discussão |
| 11:30 - 12:00 | 30min | HTTP & REST Avançado | Teórico |
| 12:00 - 13:00 | 1h | 🍽️ Almoço | - |
| 13:00 - 13:30 | 30min | Request/Response + Exception Handling | Teórico + Demo |
| 13:30 - 15:00 | 1h30 | JPA, Relacionamentos e Repositories | Teórico + Demo |
| 15:00 - 15:15 | 15min | ☕ Coffee Break | - |
| 15:15 - 16:15 | 1h | Exercício Prático - Blog API | Hands-on |
| 16:15 - 16:30 | 15min | Review e Q&A | Discussão |

---

## 📦 Material Necessário (Checklist Instrutor)

### Software
- [ ] PostgreSQL ou Podman com Postgres rodando
- [ ] DBeaver ou pgAdmin (cliente SQL)
- [ ] Postman/Insomnia com collections prontas
- [ ] H2 Console configurado

### Preparação
- [ ] Projeto products-api (Servlet+JDBC) do Dia 1 funcionando
- [ ] Scripts SQL de exemplo
- [ ] Diagrama ER do modelo de dados
- [ ] Exemplos de DTOs prontos

---

## 📋 Conteúdo Programático

---

### 🌱 O Ecossistema Spring — Visão Geral e Comparações

Antes de mergulhar no código, é essencial entender **o que é o Spring**, qual problema ele resolve e como ele se posiciona em relação a outros frameworks do mercado.

#### O que é o Spring Framework?

O **Spring Framework** é uma plataforma abrangente para desenvolvimento de aplicações Java empresariais. Criado por **Rod Johnson** em 2003 como alternativa ao J2EE (hoje Jakarta EE), o Spring se tornou o framework mais utilizado no ecossistema Java.

| Conceito | Descrição |
|----------|----------|
| **IoC (Inversão de Controle)** | O framework controla o ciclo de vida dos objetos, não o desenvolvedor |
| **DI (Injeção de Dependência)** | As dependências são fornecidas pelo container, não instanciadas manualmente |
| **AOP (Programação Orientada a Aspectos)** | Permite separar preocupações transversais (logging, segurança, transações) |
| **Convention over Configuration** | Padrões sensatos que eliminam configuração repetitiva |
| **Modularidade** | Use apenas os módulos que precisa (Web, Data, Security, Cloud, etc.) |

#### Módulos Principais do Spring

```
┌──────────────────────────────────────────────────────────┐
│                    Spring Boot                           │
│  (Auto-configuração, Starters, Embedded Server)          │
├──────────────────────────────────────────────────────────┤
│  Spring Web MVC  │  Spring Data  │  Spring Security      │
│  (REST APIs)     │  (JPA, Mongo) │  (Auth, OAuth2)        │
├──────────────────────────────────────────────────────────┤
│  Spring AOP      │  Spring TX    │  Spring Cloud          │
│  (Aspectos)      │  (Transações) │  (Microservices)       │
├──────────────────────────────────────────────────────────┤
│              Spring Framework Core                       │
│      (IoC Container, Beans, Context, SpEL)               │
└──────────────────────────────────────────────────────────┘
```

#### IoC e DI em Profundidade

**Sem Spring (acoplamento forte):**
```java
public class OrderService {
    private final OrderRepository repository = new OrderRepositoryImpl(); // ❌ Acoplado
    private final EmailService emailService = new EmailServiceImpl();     // ❌ Acoplado
}
```

**Com Spring (inversão de controle):**
```java
@Service
public class OrderService {
    private final OrderRepository repository;   // ✅ Interface
    private final EmailService emailService;     // ✅ Interface

    public OrderService(OrderRepository repository, EmailService emailService) {
        this.repository = repository;            // ✅ Injetado pelo container
        this.emailService = emailService;        // ✅ Injetado pelo container
    }
}
```

**Tipos de Injeção de Dependência no Spring:**

| Tipo | Anotação | Recomendação |
|------|----------|-------------|
| **Construtor** | Implícita (único construtor) | ✅ **Recomendado** — imutável, testável |
| **Setter** | `@Autowired` no setter | ⚠️ Dependências opcionais |
| **Field** | `@Autowired` no campo | ❌ Evitar — dificulta testes |

#### ApplicationContext e Ciclo de Vida dos Beans

```java
// O ApplicationContext é o container IoC do Spring
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        ApplicationContext ctx = SpringApplication.run(Application.class, args);
        // Todos os beans estão registrados e prontos
    }
}
```

**Escopos de Beans:**

| Escopo | Descrição | Uso Típico |
|--------|-----------|------------|
| `singleton` (padrão) | Uma única instância por container | Services, Repositories |
| `prototype` | Nova instância a cada injeção | Objetos com estado mutável |
| `request` | Uma instância por request HTTP | Dados de request |
| `session` | Uma instância por sessão HTTP | Dados de sessão |

#### Spring Boot vs Spring Framework

| Aspecto | Spring Framework | Spring Boot |
|---------|-----------------|-------------|
| Configuração | Manual (XML ou Java Config) | Auto-configuração |
| Servidor | Requer servidor externo (Tomcat WAR) | Servidor embutido (JAR) |
| Dependências | Gerenciamento manual de versões | Starters com versões compatíveis |
| Produtividade | Mais controle, mais trabalho | Rápido para começar |
| Monitoramento | Configuração manual | Actuator pronto |

---

### 🔄 Comparação: Spring vs Outros Frameworks

#### Spring Boot vs Jakarta EE (antigo Java EE)

| Aspecto | Spring Boot | Jakarta EE |
|---------|-------------|------------|
| **Filosofia** | Convention over Configuration | Especificação formal (JSRs) |
| **Container** | Embedded (Tomcat, Jetty, Undertow) | Application Server (WildFly, Payara, Open Liberty) |
| **DI** | `@Autowired`, `@Component` | `@Inject`, `@Named` (CDI) |
| **REST** | `@RestController`, `@GetMapping` | `@Path`, `@GET` (JAX-RS) |
| **Persistência** | Spring Data JPA (sobre JPA) | JPA direto (EntityManager) |
| **Segurança** | Spring Security | Jakarta Security |
| **Ecossistema** | Spring Cloud, Spring Batch, etc. | MicroProfile (para microservices) |
| **Comunidade** | Muito grande, VMware/Broadcom | Comunidade Eclipse Foundation |
| **Curva de aprendizado** | Moderada | Alta |
| **Time-to-market** | Rápido | Mais lento |

```java
// Jakarta EE (JAX-RS)
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {
    @Inject
    private ProductService service;

    @GET
    public List<Product> list() {
        return service.findAll();
    }
}

// Spring Boot
@RestController
@RequestMapping("/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<Product> list() {
        return service.findAll();
    }
}
```

#### Spring Boot vs Quarkus

| Aspecto | Spring Boot | Quarkus |
|---------|-------------|----------|
| **Startup time** | ~2-5 segundos | ~0.5-1 segundo |
| **Memória (RSS)** | ~150-300 MB | ~30-80 MB |
| **Compilação nativa** | GraalVM (suporte crescente) | GraalVM (first-class citizen) |
| **Dev Experience** | DevTools (restart) | Dev Mode (hot reload real) |
| **Ecossistema** | Gigantesco, maduro | Crescente, moderno |
| **Reatividade** | WebFlux (Project Reactor) | Mutiny (nativo) |
| **Standards** | Mix Spring + Jakarta | Jakarta EE + MicroProfile |
| **Cloud Native** | Spring Cloud | Kubernetes-native |
| **Ideal para** | Aplicações empresariais, monólitos, microservices | Microservices, serverless, containers |

```java
// Quarkus (usa anotações Jakarta EE + CDI)
@Path("/products")
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {
    @Inject
    ProductService service;

    @GET
    public List<Product> list() {
        return service.findAll();
    }
}

// Quarkus Panache (equivalente ao Spring Data)
@ApplicationScoped
public class ProductRepository implements PanacheRepository<Product> {
    public List<Product> findByCategory(String category) {
        return find("category", category).list();
    }
}
```

#### Spring Boot vs Micronaut

| Aspecto | Spring Boot | Micronaut |
|---------|-------------|----------|
| **DI** | Runtime (reflection) | Compile-time (annotation processing) |
| **Startup** | Mais lento | Ultra rápido (<1s) |
| **Memória** | Maior consumo | Baixo consumo |
| **AOP** | Runtime proxies | Compile-time |
| **Compatibilidade** | Maior ecossistema de libs | Compatível com muitas libs Spring |
| **Nativo** | GraalVM (suporte) | GraalVM (otimizado) |
| **Ideal para** | Aplicações de qualquer porte | Microservices, serverless, IoT |

```java
// Micronaut
@Controller("/products")
public class ProductController {
    private final ProductService service;

    public ProductController(ProductService service) { // DI por construtor
        this.service = service;
    }

    @Get
    public List<Product> list() {
        return service.findAll();
    }
}
```

#### Tabela Resumo Comparativa

| Critério | Spring Boot | Jakarta EE | Quarkus | Micronaut |
|----------|-------------|------------|---------|----------|
| **Maturidade** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Comunidade** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ |
| **Performance** | ⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Produtividade** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ |
| **Cloud Native** | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ |
| **Mercado de trabalho** | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐ |
| **Curva de aprendizado** | Moderada | Alta | Moderada | Moderada |

> **💡 Por que Spring Boot?** Para a maioria dos projetos empresariais, Spring Boot oferece o melhor equilíbrio entre produtividade, ecossistema e mercado de trabalho. Quarkus e Micronaut são excelentes para cenários cloud-native e serverless onde performance de startup e consumo de memória são críticos.

---

### 🧩 Conceitos Avançados do Spring

#### Profiles e Configuração por Ambiente

```yaml
# application.yml (padrão)
spring:
  profiles:
    active: dev

---
# application-dev.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
  jpa:
    show-sql: true

---
# application-prod.yml
spring:
  datasource:
    url: jdbc:postgresql://prod-server:5432/mydb
  jpa:
    show-sql: false
```

```java
@Configuration
@Profile("dev")
public class DevConfig {
    @Bean
    public DataSource dataSource() {
        return new EmbeddedDatabaseBuilder()
            .setType(EmbeddedDatabaseType.H2)
            .build();
    }
}
```

#### Spring AOP — Programação Orientada a Aspectos

```java
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.example.service.*.*(..))")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object result = joinPoint.proceed();
        long duration = System.currentTimeMillis() - start;
        log.info("{} executado em {}ms", joinPoint.getSignature(), duration);
        return result;
    }
}
```

#### Spring Events — Comunicação Desacoplada

```java
// Evento
public record OrderCreatedEvent(Long orderId, String customerEmail) {}

// Publicador
@Service
public class OrderService {
    private final ApplicationEventPublisher publisher;

    public Order createOrder(Order order) {
        Order saved = repository.save(order);
        publisher.publishEvent(new OrderCreatedEvent(saved.getId(), saved.getCustomerEmail()));
        return saved;
    }
}

// Listener
@Component
public class NotificationListener {
    @EventListener
    public void onOrderCreated(OrderCreatedEvent event) {
        emailService.sendConfirmation(event.customerEmail(), event.orderId());
    }
}
```

#### @Transactional — Gerenciamento de Transações

```java
@Service
public class TransferService {

    @Transactional // Se qualquer operação falhar, tudo é revertido
    public void transfer(Long fromId, Long toId, BigDecimal amount) {
        Account from = accountRepository.findById(fromId).orElseThrow();
        Account to = accountRepository.findById(toId).orElseThrow();
        
        from.debit(amount);
        to.credit(amount);
        
        accountRepository.save(from);
        accountRepository.save(to);
    }

    @Transactional(readOnly = true) // Otimização para leitura
    public List<Account> findAll() {
        return accountRepository.findAll();
    }
}
```

| Atributo | Descrição | Padrão |
|----------|-----------|--------|
| `propagation` | Como a transação se comporta com transações existentes | `REQUIRED` |
| `isolation` | Nível de isolamento | `DEFAULT` (do banco) |
| `readOnly` | Otimização para operações de leitura | `false` |
| `rollbackFor` | Exceptions que causam rollback | `RuntimeException` |
| `timeout` | Tempo máximo da transação (segundos) | `-1` (sem limite) |

#### Spring Actuator — Monitoramento e Health Checks

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

```yaml
management:
  endpoints:
    web:
      exposure:
        include: health, info, metrics, env
  endpoint:
    health:
      show-details: always
```

| Endpoint | Descrição |
|----------|----------|
| `/actuator/health` | Status da aplicação e dependências |
| `/actuator/info` | Informações da aplicação |
| `/actuator/metrics` | Métricas (JVM, HTTP, etc.) |
| `/actuator/env` | Variáveis de ambiente |
| `/actuator/beans` | Todos os beans registrados |
| `/actuator/mappings` | Todos os endpoints mapeados |

---

### Manhã (3 horas)

#### 1. Spring Boot Fundamentals (1.5h) - NOVO!
- **Fundamentos Web**
  - HTTP, Servlets, REST vs SOAP vs GraphQL
  - IoC e DI (Inversão de Controle, Injeção de Dependência)
  - ApplicationContext e ciclo de vida dos Beans
  - AOP (Programação Orientada a Aspectos)
  - Auto-configuração e Starters

- **Spring no Contexto do Mercado**
  - Spring Boot vs Jakarta EE (Java EE): filosofia e anotações
  - Spring Boot vs Quarkus: performance e cloud-native
  - Spring Boot vs Micronaut: DI compile-time vs runtime
  - Quando escolher cada framework

- **Primeiro Projeto Spring Boot**
  - Spring Initializr
  - Estrutura do projeto
  - @SpringBootApplication, @RestController, @Service, @Repository
  - Escopos de Beans (singleton, prototype, request, session)
  
- **Primeira API REST com Spring Boot**
  - Entity, Repository, DTOs (Records), Service, Controller
  - CRUD completo
  - Profiles e DevTools
  - Actuator para monitoramento

#### 2. Spring Web Avançado (1h)
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

- **Conceitos Transversais**
  - @Transactional e gerenciamento de transações
  - Spring Events para comunicação desacoplada
  - AOP para logging, auditoria e métricas

#### 3. Spring Data JPA (1.5h)
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

#### 4. Spring Data Repositories (1h)
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

#### 5. DTOs e Mapeamento (1h)
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
- [Spring Framework Reference](https://docs.spring.io/spring-framework/reference/)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [JPA Relationships](https://www.baeldung.com/jpa-hibernate-associations)
- [Bean Validation](https://www.baeldung.com/javax-validation)

### Leitura Complementar
- [Spring IoC Container](https://www.baeldung.com/inversion-control-and-dependency-injection-in-spring)
- [Spring AOP](https://www.baeldung.com/spring-aop)
- [Spring Profiles](https://www.baeldung.com/spring-profiles)
- [Spring Boot Actuator](https://www.baeldung.com/spring-boot-actuators)
- [Quarkus vs Spring Boot](https://www.baeldung.com/spring-boot-vs-quarkus)
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

- ✅ Entender os fundamentos do Spring (IoC, DI, AOP, ApplicationContext)
- ✅ Comparar Spring Boot com outros frameworks (Jakarta EE, Quarkus, Micronaut)
- ✅ Criar entidades JPA com relacionamentos complexos
- ✅ Desenvolver repositories customizados
- ✅ Implementar paginação e ordenação
- ✅ Usar DTOs para separar camadas
- ✅ Validar dados de entrada
- ✅ Tratar exceções de forma global e consistente
- ✅ Utilizar @Transactional para gerenciamento de transações
- ✅ Configurar Actuator para monitoramento da aplicação
- ✅ Trabalhar com Profiles para diferentes ambientes

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
