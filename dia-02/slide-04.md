# Slide 4: Exception Handling Global

**Horário:** 09:55 - 10:15

---

## ❌ O Problema: Exceções Não Tratadas

```java
// ❌ SEM tratamento global
@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return service.findById(id);  // Se não existir? 💥
    }
}

// Cliente recebe:
{
  "timestamp": "2026-02-04T10:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "path": "/api/products/999"
}
// ❌ Pouca informação, status code errado (deveria ser 404)
```

---

## ✅ Solução: @ControllerAdvice

```mermaid
flowchart TD
    A[Request] --> B[Controller]
    B --> C{Exception?}
    C -->|SIM| D[@ControllerAdvice<br/>captura]
    C -->|NÃO| E[Response OK]
    
    D --> F{Tipo de<br/>Exception}
    F -->|NotFoundException| G[404 + JSON]
    F -->|ValidationException| H[400 + JSON]
    F -->|BusinessException| I[422 + JSON]
    F -->|Outras| J[500 + JSON]
    
    style D fill:#FFB6C1
    style G fill:#FFD700
    style H fill:#FFD700
    style I fill:#FFD700
    style J fill:#FF6B6B
```

---

## 🎬 DEMO: Exception Handler Completo

### 1. Exceções Customizadas

```java
// src/main/java/com/example/exception/ResourceNotFoundException.java
package com.example.exception;

public class ResourceNotFoundException extends RuntimeException {
    private final String resourceName;
    private final String fieldName;
    private final Object fieldValue;
    
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }
    
    // Getters...
}

// BusinessException.java
public class BusinessException extends RuntimeException {
    private final String code;
    
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
    
    public String getCode() { return code; }
}
```

---

### 2. DTOs de Erro

```java
// ErrorResponse.java
package com.example.dto.response;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(
    LocalDateTime timestamp,
    int status,
    String error,
    String message,
    String path,
    List<FieldError> fieldErrors
) {
    public ErrorResponse(int status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path, null);
    }
    
    public record FieldError(String field, String message) {}
}
```

---

### 3. Global Exception Handler

```java
// src/main/java/com/example/exception/GlobalExceptionHandler.java
package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    
    // 404 - Resource Not Found
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(
            ResourceNotFoundException ex,
            WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
    
    // 400 - Validation Error
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex,
            WebRequest request) {
        
        List<ErrorResponse.FieldError> fieldErrors = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error -> new ErrorResponse.FieldError(
                error.getField(),
                error.getDefaultMessage()
            ))
            .collect(Collectors.toList());
        
        ErrorResponse error = new ErrorResponse(
            LocalDateTime.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Validation Failed",
            "One or more fields have validation errors",
            request.getDescription(false).replace("uri=", ""),
            fieldErrors
        );
        
        return ResponseEntity.badRequest().body(error);
    }
    
    // 422 - Business Exception
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(
            BusinessException ex,
            WebRequest request) {
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            ex.getCode(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }
    
    // 409 - Conflict (duplicata)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
            DataIntegrityViolationException ex,
            WebRequest request) {
        
        String message = "Data integrity violation. Possibly duplicate entry.";
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "Conflict",
            message,
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }
    
    // 500 - Internal Server Error (fallback)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(
            Exception ex,
            WebRequest request) {
        
        // Log do erro real (não expor detalhes ao cliente!)
        log.error("Unexpected error occurred", ex);
        
        ErrorResponse error = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected error occurred. Please try again later.",
            request.getDescription(false).replace("uri=", "")
        );
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
```

---

## 🎯 Usando as Exceções no Service

```java
@Service
public class ProductService {
    
    private final ProductRepository repository;
    
    public ProductResponse findById(Long id) {
        Product product = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "id", id));
        
        return ProductResponse.from(product);
    }
    
    public ProductResponse create(CreateProductRequest request) {
        // Verificar duplicata
        if (repository.existsByName(request.name())) {
            throw new BusinessException(
                "PRODUCT_DUPLICATE",
                "Product with name '" + request.name() + "' already exists"
            );
        }
        
        // Regra de negócio
        if (request.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException(
                "INVALID_PRICE",
                "Product price must be greater than zero"
            );
        }
        
        Product product = new Product(...);
        Product saved = repository.save(product);
        
        return ProductResponse.from(saved);
    }
}
```

---

## 📊 Respostas de Erro Padronizadas

### 404 - Not Found
```json
{
  "timestamp": "2026-02-04T10:15:00",
  "status": 404,
  "error": "Not Found",
  "message": "Product not found with id: '999'",
  "path": "/api/products/999",
  "fieldErrors": null
}
```

### 400 - Validation Error
```json
{
  "timestamp": "2026-02-04T10:15:00",
  "status": 400,
  "error": "Validation Failed",
  "message": "One or more fields have validation errors",
  "path": "/api/products",
  "fieldErrors": [
    {
      "field": "name",
      "message": "Name is required"
    },
    {
      "field": "price",
      "message": "Price must be greater than 0"
    }
  ]
}
```

### 422 - Business Exception
```json
{
  "timestamp": "2026-02-04T10:15:00",
  "status": 422,
  "error": "PRODUCT_DUPLICATE",
  "message": "Product with name 'Laptop' already exists",
  "path": "/api/products",
  "fieldErrors": null
}
```

---

## 💡 Boas Práticas Exception Handling

### ✅ FAÇA

```java
// 1. Use exceções específicas
throw new ResourceNotFoundException("Product", "id", id);

// 2. Mensagens claras e úteis
throw new BusinessException("INSUFFICIENT_STOCK", 
    "Cannot order 10 units. Only 5 in stock.");

// 3. Log erros inesperados
@ExceptionHandler(Exception.class)
public ResponseEntity<?> handleUnexpected(Exception ex) {
    log.error("Unexpected error", ex);  // ✅ Log completo
    return ResponseEntity.status(500)
        .body("An error occurred");  // Mensagem genérica ao cliente
}

// 4. Status codes corretos
404 - Not Found (recurso não existe)
400 - Bad Request (validação falhou)
422 - Unprocessable Entity (regra de negócio)
409 - Conflict (duplicata)
500 - Internal Server Error (erro inesperado)
```

### ❌ EVITE

```java
// 1. Expor stack traces ao cliente
catch (Exception ex) {
    return ex.getMessage();  // ❌ Pode vazar info sensível
}

// 2. Exceções genéricas
throw new RuntimeException("Error");  // ❌ Pouca informação

// 3. Misturar status codes
throw new ResourceNotFoundException()  // mas retorna 500 ❌

// 4. Não logar erros
@ExceptionHandler(Exception.class)
public ResponseEntity<?> handle(Exception ex) {
    return ResponseEntity.status(500).build();  // ❌ Erro sumiu!
}
```
