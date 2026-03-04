# Slide 8: Tratamento de Erros Global

**Horário:** 11:30 - 11:45

---

## 🚨 O Problema: Erros Espalhados

> "O tratamento de erros é importante, mas se ele obscurece a lógica, está errado."
> — **Robert C. Martin**, Clean Code (Cap. 7)

### Antes: cada Controller repete lógica de erro

```java
// ❌ Cada controller trata seus próprios erros — DUPLICAÇÃO!
@GetMapping("/{id}")
public ResponseEntity<?> findById(@PathVariable Long id) {
    try {
        Product product = productService.findById(id);
        return ResponseEntity.ok(product);
    } catch (ProductNotFoundException e) {
        return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
        return ResponseEntity.status(500).body(Map.of("error", "Internal error"));
    }
}
// 😰 Repetir isso em TODOS os métodos de TODOS os controllers?
// 😰 E se esquecer um catch? Retorna 500 genérico ao cliente!
// 😰 Formato do erro muda? Alterar em TODOS os controllers!
```

### Visualizando o problema

```mermaid
graph TD
    subgraph "❌ Erro espalhado — try/catch em todo lugar"
        PC["ProductController"] -->|"try/catch"| PSvc["ProductService"]
        OC["OrderController"] -->|"try/catch"| OSvc["OrderService"]
        UC["UserController"] -->|"try/catch"| USvc["UserService"]
    end

    subgraph "Problemas"
        P1["❌ Código duplicado em 3+ controllers"]
        P2["❌ Formato de erro inconsistente"]
        P3["❌ Fácil esquecer um catch"]
        P4["❌ Controller poluído com try/catch"]
    end
    style PC fill:#e74c3c,color:#fff
    style OC fill:#e74c3c,color:#fff
    style UC fill:#e74c3c,color:#fff
```

---

## ✅ A Solução: @ControllerAdvice

> **@ControllerAdvice** é um **interceptor global** do Spring que captura exceções lançadas por QUALQUER controller e trata em UM ÚNICO LUGAR.

### Arquitetura do tratamento de erros global

```mermaid
graph TD
    subgraph "✅ Tratamento centralizado"
        C1["ProductController<br/>sem try/catch! ✅"]
        C2["OrderController<br/>sem try/catch! ✅"]
        C3["UserController<br/>sem try/catch! ✅"]
    end

    C1 -->|"Exception sobe"| GEH
    C2 -->|"Exception sobe"| GEH
    C3 -->|"Exception sobe"| GEH

    GEH["🛡️ GlobalExceptionHandler<br/>@RestControllerAdvice"]

    GEH --> R1["404 ProblemDetail"]
    GEH --> R2["409 ProblemDetail"]
    GEH --> R3["400 ProblemDetail"]
    GEH --> R4["500 ProblemDetail"]

    style GEH fill:#2ecc71,color:#fff,stroke-width:3px
    style C1 fill:#3498db,color:#fff
    style C2 fill:#3498db,color:#fff
    style C3 fill:#3498db,color:#fff
```

---

### Implementação Completa

```java
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ── NOT FOUND (404) ──────────────────────────────────────
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            ProductNotFoundException ex, HttpServletRequest request) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, ex.getMessage()
        );
        problem.setTitle("Resource Not Found");
        problem.setType(URI.create("https://api.example.com/errors/not-found"));
        problem.setInstance(URI.create(request.getRequestURI()));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problem);
    }

    // ── CONFLICT (409) ──────────────────────────────────────
    @ExceptionHandler(DuplicateSkuException.class)
    public ResponseEntity<ProblemDetail> handleDuplicate(
            DuplicateSkuException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, ex.getMessage()
        );
        problem.setTitle("Duplicate Resource");
        return ResponseEntity.status(HttpStatus.CONFLICT).body(problem);
    }

    // ── VALIDATION ERROR (400) ──────────────────────────────
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidation(
            MethodArgumentNotValidException ex) {
        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.BAD_REQUEST, "One or more fields are invalid"
        );
        problem.setTitle("Validation Error");

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage())
        );
        problem.setProperty("errors", errors);

        return ResponseEntity.badRequest().body(problem);
    }

    // ── CATCH-ALL (500) ─────────────────────────────────────
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleUnexpected(Exception ex) {
        // Log para debugging (nunca expor stacktrace ao cliente!)
        log.error("Unexpected error", ex);

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected error occurred. Please try again later."
        );
        problem.setTitle("Internal Server Error");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problem);
    }
}
```

---

## O Controller fica LIMPO

```java
// ✅ Sem try/catch! A exceção "sobe" e o @ControllerAdvice trata.
@GetMapping("/{id}")
public ResponseEntity<ProductResponse> findById(@PathVariable Long id) {
    return ResponseEntity.ok(productService.findById(id));
    // Se ProductNotFoundException → @ControllerAdvice retorna 404
}

@PostMapping
public ResponseEntity<ProductResponse> create(
        @Valid @RequestBody ProductRequest request) {
    ProductResponse response = productService.create(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
    // Se DuplicateSkuException → @ControllerAdvice retorna 409
    // Se @Valid falhar → @ControllerAdvice retorna 400
}

@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) {
    productService.delete(id);
    return ResponseEntity.noContent().build();
    // Se ProductNotFoundException → 404 automático
}
```

> **Observe:** nenhum `try/catch` no Controller. Ele se preocupa **apenas** com o caminho feliz (happy path).

---

## Custom Exceptions — Hierarquia

```mermaid
classDiagram
    class RuntimeException {
        <<Java Standard>>
    }

    class BusinessException {
        <<abstract>>
        #String message
    }

    class ProductNotFoundException {
        +ProductNotFoundException(Long id)
    }

    class DuplicateSkuException {
        +DuplicateSkuException(String sku)
    }

    class InsufficientStockException {
        +InsufficientStockException(String product, int requested, int available)
    }

    class InvalidOperationException {
        +InvalidOperationException(String reason)
    }

    RuntimeException <|-- BusinessException
    BusinessException <|-- ProductNotFoundException
    BusinessException <|-- DuplicateSkuException
    BusinessException <|-- InsufficientStockException
    BusinessException <|-- InvalidOperationException
```

### Implementação das Exceptions

```java
// Base — todas as exceções de negócio herdam desta
public abstract class BusinessException extends RuntimeException {
    protected BusinessException(String message) {
        super(message);
    }
}

// Exceção de negócio — NOT FOUND (404)
public class ProductNotFoundException extends BusinessException {
    public ProductNotFoundException(Long id) {
        super("Product with id " + id + " not found");
    }
}

// Exceção de negócio — CONFLICT (409)
public class DuplicateSkuException extends BusinessException {
    public DuplicateSkuException(String sku) {
        super("Product with SKU '" + sku + "' already exists");
    }
}

// Exceção de negócio — BUSINESS RULE (422)
public class InsufficientStockException extends BusinessException {
    public InsufficientStockException(String productName, int requested, int available) {
        super("Insufficient stock for '%s': requested %d, available %d"
            .formatted(productName, requested, available));
    }
}
```

> **Por que `RuntimeException`?** Exceções checked (extends `Exception`) obrigam `throws` em cada método. Exceções de negócio são unchecked para manter o código limpo.

---

## Fluxo Completo — Sequence Diagram

```mermaid
sequenceDiagram
    participant Client as 🌐 Client
    participant Filter as 🔒 Filters
    participant Controller as 📥 Controller
    participant Service as ⚙️ Service
    participant Repo as 🛢️ Repository
    participant Handler as 🛡️ @ControllerAdvice

    Note over Client,Handler: Cenário 1: Produto encontrado (200 OK)
    Client->>Filter: GET /api/products/1
    Filter->>Controller: findById(1)
    Controller->>Service: findById(1)
    Service->>Repo: findById(1)
    Repo-->>Service: Optional.of(product)
    Service-->>Controller: ProductResponse
    Controller-->>Client: 200 OK + JSON

    Note over Client,Handler: Cenário 2: Produto não encontrado (404)
    Client->>Filter: GET /api/products/999
    Filter->>Controller: findById(999)
    Controller->>Service: findById(999)
    Service->>Repo: findById(999)
    Repo-->>Service: Optional.empty()
    Service-->>Controller: ❌ throw ProductNotFoundException
    Controller-->>Handler: Exception propagada
    Handler->>Handler: handleNotFound()
    Handler-->>Client: 404 + ProblemDetail JSON

    Note over Client,Handler: Cenário 3: Validação falhou (400)
    Client->>Filter: POST /api/products {name: ""}
    Filter->>Controller: @Valid → MethodArgumentNotValidException
    Controller-->>Handler: Exception propagada
    Handler->>Handler: handleValidation()
    Handler-->>Client: 400 + ProblemDetail + errors map
```

---

## 📏 Checklist: Exception Handling

| Prática | ✅ Correto | ❌ Evitar |
|---------|-----------|----------|
| Onde tratar | `@ControllerAdvice` centralizado | try/catch em cada method |
| Tipo de exceção | Custom (ex: `ProductNotFoundException`) | `throw new Exception("msg")` |
| Herança | `extends RuntimeException` | `extends Exception` (checked) |
| Resposta | `ProblemDetail` (RFC 7807) | `Map.of("error", msg)` ou String |
| Stacktrace | Log no servidor, **nunca** enviar ao cliente | `ex.printStackTrace()` na response |
| Catch-all | `@ExceptionHandler(Exception.class)` com log | Deixar 500 genérico do Spring (Whitelabel) |

---

## 🎯 Pergunta para a turma

> O que acontece se eu NÃO tiver um `@ControllerAdvice` e uma exceção for lançada?
> Resposta: o Spring retorna a **Whitelabel Error Page** (HTML) ou um JSON genérico sem detalhes — péssimo para APIs.

---

## 💡 Dica do Instrutor

Demonstrar ao vivo: chamar um endpoint com ID inexistente e mostrar o ProblemDetail retornado. Depois, comentar o `@ControllerAdvice` e mostrar a diferença (Whitelabel Page ou JSON genérico).
