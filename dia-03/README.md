# Dia 3 - Arquitetura, Clean Code, Refatoração e Padronização

**Duração**: 5 horas  
**Objetivo**: Sair do "código que funciona" para o "código que escala" — aplicando Clean Code, arquitetura em camadas, hexagonal, tratamento de erros e validação

---

## 🎯 Agenda do Dia

| Horário | Duração | Tópico | Tipo |
|---------|---------|--------|------|
| 09:00 - 09:15 | 15min | Recap Dia 2 e Introdução ao Dia 3 | Discussão |
| 09:15 - 10:00 | 45min | Clean Code e Code Smells | Teórico |
| 10:00 - 10:45 | 45min | Arquitetura em Camadas e DTOs | Teórico |
| 10:45 - 11:00 | 15min | ☕ Coffee Break | - |
| 11:00 - 11:30 | 30min | Arquitetura Hexagonal (Ports & Adapters) | Teórico |
| 11:30 - 12:00 | 30min | Tratamento de Erros Global e Validação | Teórico |
| 12:00 - 13:00 | 1h | 🍽️ Almoço | - |
| 13:00 - 13:30 | 30min | Refactoring — Técnicas e Ferramentas | Teórico |
| 13:30 - 14:00 | 30min | Walkthrough `03-clean-architecture-demo` | Demo |
| 14:00 - 15:30 | 1h30 | Exercício `03-employee-api` (TODOs 1-7) | Hands-on |
| 15:30 - 16:30 | 1h | Exercício `03-bad-practices-lab` (TODOs 1-9) | Hands-on |
| 16:30 - 17:00 | 30min | Review e Q&A | Discussão |

---

## 📦 Material Necessário (Checklist Instrutor)

### Software
- [ ] JDK 21 instalado
- [ ] Maven 3.8+
- [ ] IDE com suporte a refatoração (IntelliJ recomendado)
- [ ] Postman/Insomnia/VS Code REST Client

### Preparação
- [ ] Projeto `03-clean-architecture-demo` rodando
- [ ] Projeto `03-employee-api` com TODOs prontos
- [ ] Projeto `03-bad-practices-lab` com testes passando
- [ ] Diagrama de Arquitetura Hexagonal preparado

---

## 📋 Conteúdo Programático

---

### 1. Clean Code — Escrevendo Código Profissional

O Clean Code não é sobre escrever código bonito — é sobre escrever código que **outros desenvolvedores conseguem entender, manter e evoluir** sem medo de quebrar algo.

#### Nomenclatura Significativa

Variáveis, métodos e classes devem explicar o **"porquê"**, não o **"como"**.

```java
// ❌ Nomes sem significado
int d; // dias desde a última compra
List<int[]> list1; // lista de algo...
String tp; // tipo de pagamento

// ✅ Nomes que explicam a intenção
int daysSinceLastPurchase;
List<int[]> flaggedCells;
String paymentType;
```

#### Métodos Pequenos e Coesos

Uma função faz **uma coisa** — máximo ~20 linhas.

```java
// ❌ God Method — faz tudo em 100+ linhas
public Order processOrder(OrderRequest request) {
    // valida estoque (20 linhas)
    // calcula desconto (15 linhas)
    // calcula frete (25 linhas)
    // salva no banco (10 linhas)
    // envia email (15 linhas)
    // atualiza estoque (10 linhas)
}

// ✅ Cada método faz UMA coisa
public Order processOrder(OrderRequest request) {
    validateStock(request.items());
    BigDecimal discount = calculateDiscount(request);
    BigDecimal shipping = calculateShipping(request.address());
    Order order = createOrder(request, discount, shipping);
    notifyCustomer(order);
    updateStock(request.items());
    return order;
}
```

#### A Regra do Escoteiro

> "Deixe o código melhor do que encontrou."

Sempre que tocar em um arquivo, melhore algo: renomeie uma variável, extraia um método, remova código morto.

#### Code Smells

| Code Smell | Descrição | Solução |
|------------|-----------|---------|
| **God Class** | Classe com 500+ linhas e muitas responsabilidades | Separar em classes menores (SRP) |
| **Long Method** | Método com 50+ linhas | Extract Method |
| **Feature Envy** | Método que usa mais dados de outra classe do que da própria | Move Method |
| **Primitive Obsession** | Usar `String` para CPF, Email, Money | Value Objects (Records) |
| **Dead Code** | Código comentado ou nunca executado | Deletar (Git guarda o histórico) |

#### DRY vs. WET

```java
// ❌ WET (Write Everything Twice)
public BigDecimal calculateRegularDiscount(BigDecimal price) {
    return price.multiply(BigDecimal.valueOf(0.10));
}
public BigDecimal calculateVipDiscount(BigDecimal price) {
    return price.multiply(BigDecimal.valueOf(0.10)); // mesma lógica!
}

// ✅ DRY (Don't Repeat Yourself)
public BigDecimal calculateDiscount(BigDecimal price, BigDecimal rate) {
    return price.multiply(rate);
}
```

---

### 2. Arquitetura em Camadas — O Padrão das Consultorias

#### Fluxo Controller → Service → Repository

```
┌──────────────────────────────────────────────────────────┐
│  Controller (Web Layer)                                  │
│  - Recebe HTTP Request                                   │
│  - Valida entrada com @Valid                             │
│  - Delega para o Service                                │
│  - Retorna HTTP Response                                │
├──────────────────────────────────────────────────────────┤
│  Service (Business Layer)                                │
│  - Aplica regras de negócio                             │
│  - Orquestra chamadas ao Repository                     │
│  - Converte Entity ↔ DTO                               │
│  - Gerencia transações (@Transactional)                 │
├──────────────────────────────────────────────────────────┤
│  Repository (Data Layer)                                 │
│  - Acessa o banco de dados                              │
│  - CRUD operations via JPA                              │
│  - Queries customizadas                                 │
└──────────────────────────────────────────────────────────┘
```

#### Por que nunca expor a Entity JPA no Controller

```java
// ❌ Entity exposta — problemas:
// 1. Acoplamento: mudança no banco afeta a API
// 2. Segurança: campos internos (password, tokens) vazam
// 3. Evolução: não consegue versionar a API
@GetMapping("/{id}")
public Product getProduct(@PathVariable Long id) {
    return productRepository.findById(id).orElseThrow();
}

// ✅ DTO separado — limpo e seguro
@GetMapping("/{id}")
public ProductResponse getProduct(@PathVariable Long id) {
    return productService.findById(id); // retorna DTO
}
```

#### DTOs (Data Transfer Objects)

```java
// Request DTO (entrada)
public record ProductRequest(
    @NotBlank String name,
    @NotBlank String sku,
    @Positive BigDecimal price,
    @Size(max = 500) String description
) {}

// Response DTO (saída)
public record ProductResponse(
    Long id,
    String name,
    String sku,
    BigDecimal price,
    String description,
    LocalDateTime createdAt
) {
    public static ProductResponse from(Product entity) {
        return new ProductResponse(
            entity.getId(), entity.getName(), entity.getSku(),
            entity.getPrice(), entity.getDescription(), entity.getCreatedAt()
        );
    }
}
```

#### Mapeamento Entity ↔ DTO

| Abordagem | Prós | Contras |
|-----------|------|---------|
| **Manual** | Simples, sem dependência | Repetitivo, propenso a erro |
| **ModelMapper** | Automático, convenção | Reflexão, difícil debugar |
| **MapStruct** | Gerado em compile-time, type-safe | Configuração inicial |

```java
// Mapper manual (recomendado para começar)
public class ProductMapper {
    public static Product toEntity(ProductRequest request) {
        Product product = new Product();
        product.setName(request.name());
        product.setSku(request.sku());
        product.setPrice(request.price());
        product.setDescription(request.description());
        return product;
    }
    
    public static ProductResponse toResponse(Product entity) {
        return new ProductResponse(
            entity.getId(), entity.getName(), entity.getSku(),
            entity.getPrice(), entity.getDescription(), entity.getCreatedAt()
        );
    }
}
```

---

### 3. Introdução à Arquitetura Hexagonal (Ports & Adapters)

#### O Problema do "Service que faz tudo"

```java
// ❌ Service acoplado à infraestrutura
@Service
public class ProductService {
    private final JpaRepository<Product, Long> repository; // JPA direto
    private final RestTemplate restTemplate; // HTTP direto
    private final JavaMailSender mailSender; // SMTP direto
    
    // Regras de negócio MISTURADAS com infraestrutura
}
```

#### Conceito de Ports e Adapters

```
┌──────────────────────────────────────────────────────────────┐
│                     Adapter IN (Web)                         │
│              ProductController (REST API)                    │
│                   ↓ usa Port IN                              │
├──────────────────────────────────────────────────────────────┤
│                       DOMAIN                                 │
│    ┌─────────────────────────────────────────────────┐       │
│    │  Port IN:     ProductUseCase (interface)        │       │
│    │  Domain:      ProductService (implementa)       │       │
│    │  Port OUT:    ProductRepository (interface)     │       │
│    └─────────────────────────────────────────────────┘       │
├──────────────────────────────────────────────────────────────┤
│                    Adapter OUT (Persistence)                  │
│            JpaProductRepository (implementa Port OUT)        │
│                   Spring Data JPA                            │
└──────────────────────────────────────────────────────────────┘
```

#### Estrutura de Pacotes

```
com.example.products/
├── domain/
│   ├── model/
│   │   └── Product.java              (entidade de domínio)
│   ├── port/
│   │   ├── in/
│   │   │   └── ProductUseCase.java   (interface de entrada)
│   │   └── out/
│   │       └── ProductRepository.java (interface de saída)
│   ├── service/
│   │   └── ProductService.java       (regras de negócio)
│   └── exception/
│       └── ProductNotFoundException.java
├── adapter/
│   ├── in/
│   │   └── web/
│   │       ├── ProductController.java
│   │       ├── ProductRequest.java  (DTO)
│   │       └── ProductResponse.java (DTO)
│   └── out/
│       └── persistence/
│           ├── JpaProductRepository.java
│           ├── ProductJpaEntity.java
│           └── ProductMapper.java
└── config/
    └── BeanConfig.java
```

#### Quando usar Hexagonal vs. Camadas Simples

| Cenário | Recomendação |
|---------|--------------|
| CRUD simples, MVP, API interna | Camadas simples |
| Domínio complexo, muitas regras | Hexagonal |
| Múltiplas fontes de dados | Hexagonal |
| Equipe grande, longo prazo | Hexagonal |
| Prova de conceito, hackathon | Camadas simples |

---

### 4. Tratamento de Erros Global

#### @ControllerAdvice + @ExceptionHandler

```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(ProductNotFoundException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage()
        );
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("https://api.example.com/errors/not-found"));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "Validation failed"
        );
        problem.setTitle("Validation Error");
        
        List<String> errors = ex.getBindingResult().getFieldErrors().stream()
            .map(f -> f.getField() + ": " + f.getDefaultMessage())
            .toList();
        problem.setProperty("errors", errors);
        
        return ResponseEntity.badRequest().body(problem);
    }
}
```

#### Problem Details (RFC 7807)

Resposta padronizada de erro — formato JSON que qualquer cliente entende:

```json
{
  "type": "https://api.example.com/errors/not-found",
  "title": "Resource Not Found",
  "status": 404,
  "detail": "Product with id 42 not found",
  "instance": "/api/products/42"
}
```

A partir do Spring Boot 3.x, o suporte a `ProblemDetail` é nativo:
```yaml
spring:
  mvc:
    problemdetails:
      enabled: true
```

#### Custom Exceptions

```java
public class ProductNotFoundException extends RuntimeException {
    public ProductNotFoundException(Long id) {
        super("Product with id " + id + " not found");
    }
}

public class DuplicateSkuException extends RuntimeException {
    public DuplicateSkuException(String sku) {
        super("Product with SKU '" + sku + "' already exists");
    }
}
```

---

### 5. Validação de Dados

#### Bean Validation com @Valid

```java
public record ProductRequest(
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 100, message = "Name must be between 3 and 100 characters")
    String name,
    
    @NotBlank(message = "SKU is required")
    String sku,
    
    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    BigDecimal price,
    
    @Email(message = "Invalid email format")
    String contactEmail
) {}

// No Controller:
@PostMapping
public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
    // @Valid dispara validação automática antes de entrar no método
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(productService.create(request));
}
```

#### Custom Validators

```java
// 1. Anotação
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CpfValidator.class)
public @interface ValidCpf {
    String message() default "CPF inválido";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

// 2. Validator
public class CpfValidator implements ConstraintValidator<ValidCpf, String> {
    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        if (cpf == null || cpf.isBlank()) return false;
        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() != 11) return false;
        if (digits.chars().distinct().count() == 1) return false;
        // ... cálculo dos dígitos verificadores
        return true;
    }
}

// 3. Uso no DTO
public record EmployeeRequest(
    @NotBlank String name,
    @ValidCpf String cpf,
    @Email String email
) {}
```

---

### 6. Refactoring — Melhorando Código Existente

#### Técnicas de Refatoração

| Técnica | Quando Usar | Atalho IntelliJ |
|---------|-------------|-----------------|
| **Extract Method** | Método longo, bloco com comentário | `Ctrl+Alt+M` |
| **Extract Class** | Classe com muitas responsabilidades | Manual |
| **Rename** | Nome não expressa intenção | `Shift+F6` |
| **Inline** | Variável ou método desnecessário | `Ctrl+Alt+N` |
| **Move** | Método na classe errada | `F6` |
| **Replace Conditional with Polymorphism** | if/else com 5+ condições | Manual |

#### Refatoração Segura

```
                    ┌─────────────┐
                    │  TESTES     │
                    │  PASSANDO   │  ← Ponto de partida
                    │  (green)    │
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │  REFATORAR  │  ← Mudar estrutura,
                    │  (refactor) │    NÃO comportamento
                    └──────┬──────┘
                           │
                    ┌──────▼──────┐
                    │  TESTES     │
                    │  PASSANDO   │  ← Confirmar
                    │  (green)    │
                    └─────────────┘
```

> **Regra de ouro:** Nunca refatore com testes quebrados. Se algo quebrar, desfaça (Ctrl+Z) e tente novamente com passos menores.

#### Antes vs. Depois

```java
// ❌ ANTES: números mágicos + cadeia de if/else
public double calc(int t, double v) {
    if (t == 1) return v * 0.1;
    else if (t == 2) return v * 0.15;
    else if (t == 3) return v * 0.2;
    else return 0;
}

// ✅ DEPOIS: constantes + Strategy Pattern
private static final BigDecimal REGULAR_RATE = new BigDecimal("0.10");
private static final BigDecimal VIP_RATE = new BigDecimal("0.15");
private static final BigDecimal PREMIUM_RATE = new BigDecimal("0.20");

public BigDecimal calculateDiscount(DiscountType type, BigDecimal value) {
    return value.multiply(type.getRate());
}
```

---

## 📦 Projetos do Dia

### 1. `03-clean-architecture-demo` (Projeto Exemplo)
API de Catálogo de Produtos — pronta e funcionando. O aluno roda e acompanha a explicação.

```bash
cd 03-clean-architecture-demo
mvn spring-boot:run
# Porta: 8083
# Testar: api-requests.http
```

### 2. `03-employee-api` (Exercício)
API de Gestão de Funcionários — o aluno recebe a estrutura base e implementa os TODOs.

```bash
cd 03-employee-api
mvn spring-boot:run
# Porta: 8084
# Testar: api-requests.http
```

### 3. `03-bad-practices-lab` (Refatoração)
Código propositalmente ruim — o aluno identifica os problemas e refatora aplicando Clean Code.

```bash
cd 03-bad-practices-lab
mvn test  # Todos devem passar ANTES e DEPOIS da refatoração
mvn spring-boot:run
# Porta: 8085
# Testar: api-requests.http
```

---

## 📚 Material de Estudo

### Leitura Obrigatória
- [Clean Code — Robert C. Martin (resumo)](https://gist.github.com/wojteklu/73c6914cc446146b8b533c0988cf8d29)
- [Problem Details (RFC 7807)](https://www.rfc-editor.org/rfc/rfc7807)
- [Bean Validation](https://www.baeldung.com/javax-validation)
- [Spring Boot Error Handling](https://www.baeldung.com/exception-handling-for-rest-with-spring)

### Leitura Complementar
- [Hexagonal Architecture — Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Refactoring Guru — Code Smells](https://refactoring.guru/refactoring/smells)
- [MapStruct Reference](https://mapstruct.org/documentation/reference-guide/)
- [Custom Validators in Spring](https://www.baeldung.com/spring-mvc-custom-validator)
- [Strategy Pattern](https://refactoring.guru/design-patterns/strategy)

---

## 🎯 Objetivos de Aprendizagem

Ao final deste dia, você deve ser capaz de:

- ✅ Aplicar princípios de Clean Code: nomenclatura significativa, métodos coesos, DRY
- ✅ Identificar e corrigir Code Smells: God Class, Long Method, Feature Envy
- ✅ Estruturar projetos com Arquitetura em Camadas (Controller → Service → Repository)
- ✅ Usar DTOs para separar a API da camada de persistência
- ✅ Implementar mapeamento Entity ↔ DTO
- ✅ Entender Arquitetura Hexagonal (Ports & Adapters) e quando aplicá-la
- ✅ Centralizar tratamento de erros com `@ControllerAdvice` e Problem Details
- ✅ Criar Custom Exceptions de negócio
- ✅ Validar dados com Bean Validation (`@Valid`, `@NotBlank`, `@Positive`)
- ✅ Criar Custom Validators (ex: `@ValidCpf`)
- ✅ Aplicar técnicas de refatoração segura (Extract Method, Rename, Move)
- ✅ Refatorar código usando o ciclo green → refactor → green

## 🏠 Tarefa de Casa

1. **Completar os TODOs restantes** do `03-employee-api` e `03-bad-practices-lab`
2. **Estudar**:
   - Princípios SOLID completos (especialmente SRP e DIP)
   - Strategy Pattern e Template Method
   - MapStruct para mapeamento avançado
3. **Preparação para Dia 4**:
   - Ler sobre JUnit 5 e Mockito
   - Entender o conceito de Testcontainers
   - Instalar Docker (necessário para Testcontainers)

## 📝 Notas do Instrutor

```
Pontos de atenção:
- Começar com exemplos de código ruim vs. limpo para engajar
- Mostrar refatoração AO VIVO na IDE (Extract Method, Rename)
- Enfatizar que Hexagonal ≠ obrigatório — mostrar quando vale
- No bad-practices-lab, rodar testes ANTES e pedir que reforcem o ciclo
- Problem Details é nativo no Spring Boot 3.x — usar ProblemDetail.class
- Custom Validator é ótimo para mostrar o poder do Bean Validation
- Dar 5min para cada TODO e circular entre os alunos
```

## 🔗 Links Úteis

- [Refactoring Guru](https://refactoring.guru/)
- [Clean Code Summary](https://gist.github.com/wojteklu/73c6914cc446146b8b533c0988cf8d29)
- [Spring Problem Details](https://www.baeldung.com/spring-boot-3-problem-details)
- [IntelliJ Refactoring Shortcuts](https://www.jetbrains.com/help/idea/refactoring-source-code.html)
