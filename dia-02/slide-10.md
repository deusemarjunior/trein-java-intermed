# Slide 10: DTOs e Mapeamento

**Horário:** 14:00 - 14:20

---

## 🎯 Por que usar DTOs?

```mermaid
flowchart TD
    A[Por que DTOs?] --> B[Separação de Concerns]
    A --> C[Controle de Exposição]
    A --> D[Versionamento]
    A --> E[Performance]
    
    B --> B1[Entity != API Contract]
    C --> C1[Ocultar campos sensíveis]
    D --> D1[/api/v1 vs /api/v2]
    E --> E1[Evitar lazy loading exceptions]
    
    style A fill:#FFD700
```

**DTO = Data Transfer Object**
- Objetos simples para transferir dados entre camadas
- Controla exatamente o que entra e sai da API
- Desacopla modelo de domínio da API

---

## ❌ Problemas de Expor Entidades

```java
@Entity
public class User {
    private Long id;
    private String username;
    private String password;  // ❌ NUNCA expor!
    private String email;
    private LocalDateTime lastLogin;
    
    @OneToMany(fetch = FetchType.LAZY)
    private List<Order> orders;  // ❌ LazyInitializationException
    
    @ManyToOne
    private Company company;  // ❌ Serialização infinita
}

// Controller ❌ NÃO FAÇA ISSO
@GetMapping("/{id}")
public User findById(@PathVariable Long id) {
    return userRepository.findById(id).orElseThrow();
    // Problemas:
    // 1. Expõe password
    // 2. Lazy loading exception com orders
    // 3. Pode serializar company → users → company → ...
    // 4. Mudança na entity quebra a API
}
```

---

## ✅ Solução: Request e Response DTOs

```java
// Entity (nunca expor!)
@Entity
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private LocalDateTime createdAt;
    
    @OneToMany
    private List<Order> orders;
}

// Request DTO (dados de entrada)
public record CreateUserRequest(
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 50)
    String username,
    
    @Email(message = "Email inválido")
    @NotBlank
    String email,
    
    @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[0-9]).*$", 
             message = "Senha deve ter letra maiúscula e número")
    String password
) {}

// Response DTO (dados de saída)
public record UserResponse(
    Long id,
    String username,
    String email,
    LocalDateTime createdAt
) {
    // Factory method para conversão
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getCreatedAt()
        );
    }
}

// Controller ✅ Usando DTOs
@PostMapping
public ResponseEntity<UserResponse> create(@Valid @RequestBody CreateUserRequest request) {
    User user = userService.create(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(UserResponse.from(user));
}

@GetMapping("/{id}")
public ResponseEntity<UserResponse> findById(@PathVariable Long id) {
    User user = userService.findById(id);
    return ResponseEntity.ok(UserResponse.from(user));
}
```

---

## 🎨 Tipos de DTOs

### 1. Request DTOs (Input)

```java
public record CreateProductRequest(
    @NotBlank String name,
    @NotNull @Positive BigDecimal price,
    @NotNull Long categoryId
) {}

public record UpdateProductRequest(
    @NotBlank String name,
    @Positive BigDecimal price,
    Long categoryId  // Opcional no update
) {}
```

### 2. Response DTOs (Output)

```java
public record ProductResponse(
    Long id,
    String name,
    BigDecimal price,
    String categoryName,  // Denormalizado!
    LocalDateTime createdAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getPrice(),
            product.getCategory().getName(),
            product.getCreatedAt()
        );
    }
}
```

### 3. DTOs Aninhados

```java
public record OrderResponse(
    Long id,
    LocalDateTime createdAt,
    BigDecimal total,
    List<OrderItemResponse> items  // DTO aninhado
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getCreatedAt(),
            order.getTotal(),
            order.getItems().stream()
                .map(OrderItemResponse::from)
                .toList()
        );
    }
}

public record OrderItemResponse(
    Long productId,
    String productName,
    int quantity,
    BigDecimal price
) {
    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
            item.getProduct().getId(),
            item.getProduct().getName(),
            item.getQuantity(),
            item.getPrice()
        );
    }
}
```

---

## 🔄 Mapeamento Entity ↔ DTO

### Opção 1: Factory Methods (Recomendado para projetos pequenos)

```java
public record UserResponse(Long id, String name, String email) {
    
    // Entity → DTO
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getId(),
            user.getName(),
            user.getEmail()
        );
    }
}

// Uso
UserResponse response = UserResponse.from(user);
List<UserResponse> responses = users.stream()
    .map(UserResponse::from)
    .toList();
```

### Opção 2: Métodos no Service

```java
@Service
public class UserService {
    
    public User toEntity(CreateUserRequest request) {
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        return user;
    }
    
    public void updateEntity(User user, UpdateUserRequest request) {
        user.setEmail(request.email());
        if (request.username() != null) {
            user.setUsername(request.username());
        }
    }
}
```

### Opção 3: MapStruct (Para projetos maiores)

```java
@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserResponse toResponse(User user);
    
    List<UserResponse> toResponseList(List<User> users);
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    User toEntity(CreateUserRequest request);
}

// Uso no Service
@Service
public class UserService {
    private final UserMapper mapper;
    
    public UserResponse create(CreateUserRequest request) {
        User user = mapper.toEntity(request);
        User saved = repository.save(user);
        return mapper.toResponse(saved);
    }
}
```

---

## 🎬 DEMO Completo: Product CRUD

```java
// Request DTOs
public record CreateProductRequest(
    @NotBlank String name,
    @Size(max = 500) String description,
    @NotNull @Positive BigDecimal price,
    @NotNull Long categoryId
) {}

public record UpdateProductRequest(
    @NotBlank String name,
    String description,
    @Positive BigDecimal price
) {}

// Response DTO
public record ProductResponse(
    Long id,
    String name,
    String description,
    BigDecimal price,
    CategoryResponse category,
    LocalDateTime createdAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getDescription(),
            product.getPrice(),
            CategoryResponse.from(product.getCategory()),
            product.getCreatedAt()
        );
    }
}

public record CategoryResponse(Long id, String name) {
    public static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}

// Controller
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @Autowired
    private ProductService service;
    
    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody CreateProductRequest request) {
        ProductResponse response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
        ProductResponse response = service.findById(id);
        return ResponseEntity.ok(response);
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody UpdateProductRequest request) {
        ProductResponse response = service.update(id, request);
        return ResponseEntity.ok(response);
    }
}

// Service
@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public ProductResponse create(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
            .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        
        Product product = new Product();
        product.setName(request.name());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(category);
        
        Product saved = productRepository.save(product);
        return ProductResponse.from(saved);
    }
    
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        return ProductResponse.from(product);
    }
    
    public ProductResponse update(Long id, UpdateProductRequest request) {
        Product product = productRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Product not found"));
        
        product.setName(request.name());
        product.setDescription(request.description());
        if (request.price() != null) {
            product.setPrice(request.price());
        }
        
        Product updated = productRepository.save(product);
        return ProductResponse.from(updated);
    }
}
```

---

## 🎯 Boas Práticas

### ✅ Faça:

1. **Sempre use DTOs em Controllers**
2. **Nomeie claramente:** `CreateXRequest`, `UpdateXRequest`, `XResponse`
3. **Valide requests** com Bean Validation
4. **Factory methods** para conversões simples
5. **Records** para DTOs (imutáveis, concisos)

### ❌ Evite:

1. **Expor entities** diretamente
2. **DTOs genéricos** (`Map<String, Object>`)
3. **Lógica de negócio** em DTOs
4. **Reutilizar** mesmo DTO para create e update
5. **Muitos campos opcionais** - crie DTOs específicos

---

## 🏋️ Exercício (10 min)

Crie DTOs completos para **Task**:

```java
// Entity (já existe)
@Entity
public class Task {
    private Long id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDate dueDate;
    @ManyToOne
    private User assignee;
    private LocalDateTime createdAt;
}

// Criar:
// 1. CreateTaskRequest (com validações)
// 2. UpdateTaskRequest
// 3. TaskResponse (com nome do assignee)
// 4. TaskSummaryResponse (apenas id, title, status)
```

**Próximo:** Coffee Break (15:00-15:15) →
