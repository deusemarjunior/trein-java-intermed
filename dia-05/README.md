# Dia 5 - API Design e Integrações

**Duração**: 5 horas  
**Objetivo**: Dominar design de APIs, segurança e integrações com serviços externos

## 📋 Conteúdo Programático

### Manhã (3 horas)

#### 1. Contract-First com OpenAPI/Swagger (1.5h)

**Por que Contract-First?**
- API como contrato entre frontend e backend
- Desenvolvimento paralelo
- Documentação sempre atualizada
- Geração automática de código

**OpenAPI Specification (YAML)**
```yaml
openapi: 3.0.3
info:
  title: E-commerce API
  version: 1.0.0
  description: API para gerenciamento de e-commerce
  
servers:
  - url: http://localhost:8080/api/v1
    description: Development server

paths:
  /products:
    get:
      summary: Lista produtos
      operationId: listProducts
      tags:
        - Products
      parameters:
        - name: page
          in: query
          schema:
            type: integer
            default: 0
        - name: size
          in: query
          schema:
            type: integer
            default: 20
      responses:
        '200':
          description: Lista de produtos
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProductPage'
    post:
      summary: Cria produto
      operationId: createProduct
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateProductRequest'
      responses:
        '201':
          description: Produto criado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProductResponse'
        '400':
          description: Requisição inválida
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

components:
  schemas:
    ProductResponse:
      type: object
      required:
        - id
        - name
        - price
      properties:
        id:
          type: string
          format: uuid
        name:
          type: string
        description:
          type: string
        price:
          type: number
          format: double
        category:
          type: string
        createdAt:
          type: string
          format: date-time
    
    CreateProductRequest:
      type: object
      required:
        - name
        - price
      properties:
        name:
          type: string
          minLength: 3
          maxLength: 100
        description:
          type: string
          maxLength: 500
        price:
          type: number
          minimum: 0
        categoryId:
          type: string
          format: uuid
    
    ErrorResponse:
      type: object
      properties:
        message:
          type: string
        errors:
          type: array
          items:
            type: string
        timestamp:
          type: string
          format: date-time
```

**Gerando código a partir do contrato**:
```xml
<!-- pom.xml -->
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.0.1</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/openapi/api.yaml</inputSpec>
                <generatorName>spring</generatorName>
                <apiPackage>com.example.api</apiPackage>
                <modelPackage>com.example.api.model</modelPackage>
                <configOptions>
                    <interfaceOnly>true</interfaceOnly>
                    <useSpringBoot3>true</useSpringBoot3>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

**SpringDoc OpenAPI**
```java
// Configuração
@Configuration
public class OpenApiConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("E-commerce API")
                .version("1.0.0")
                .description("API Documentation")
                .contact(new Contact()
                    .name("Dev Team")
                    .email("dev@example.com")))
            .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
            .components(new Components()
                .addSecuritySchemes("bearerAuth", 
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}

// Anotações em Controllers
@Operation(summary = "Create a new product", 
           description = "Creates a new product in the catalog")
@ApiResponses(value = {
    @ApiResponse(responseCode = "201", description = "Product created"),
    @ApiResponse(responseCode = "400", description = "Invalid input"),
    @ApiResponse(responseCode = "401", description = "Unauthorized")
})
@PostMapping
public ResponseEntity<ProductResponse> create(
    @Valid @RequestBody CreateProductRequest request) {
    // ...
}
```

#### 2. REST API Best Practices (1h)

**HTTP Status Codes apropriados**
```
200 OK              - GET, PUT bem sucedidos
201 Created         - POST bem sucedido
204 No Content      - DELETE bem sucedido
400 Bad Request     - Validação falhou
401 Unauthorized    - Não autenticado
403 Forbidden       - Não autorizado
404 Not Found       - Recurso não existe
409 Conflict        - Conflito (ex: duplicação)
422 Unprocessable   - Erro de negócio
500 Internal Error  - Erro do servidor
```

**Versionamento de API**
```java
// Opção 1: URI Path
@RequestMapping("/api/v1/products")

// Opção 2: Header
@RequestMapping(value = "/api/products", 
                headers = "X-API-Version=1")

// Opção 3: Media Type
@RequestMapping(value = "/api/products",
                produces = "application/vnd.company.v1+json")
```

**HATEOAS (Hypermedia)**
```java
@GetMapping("/{id}")
public EntityModel<ProductResponse> getProduct(@PathVariable Long id) {
    Product product = productService.findById(id);
    ProductResponse response = mapper.toResponse(product);
    
    return EntityModel.of(response,
        linkTo(methodOn(ProductController.class).getProduct(id)).withSelfRel(),
        linkTo(methodOn(ProductController.class).list()).withRel("products"),
        linkTo(methodOn(CategoryController.class).getCategory(product.getCategoryId())).withRel("category"));
}
```

**Paginação, Filtragem e Ordenação**
```java
@GetMapping
public Page<ProductResponse> list(
    @RequestParam(required = false) String name,
    @RequestParam(required = false) BigDecimal minPrice,
    @RequestParam(required = false) BigDecimal maxPrice,
    @PageableDefault(size = 20, sort = "name") Pageable pageable) {
    
    return productService.search(name, minPrice, maxPrice, pageable);
}
```

#### 3. Spring Security (30min)

**Configuração básica**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        
        return http.build();
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri("https://auth-server/.well-known/jwks.json").build();
    }
}
```

**Autenticação com JWT**
```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationManager authManager;
    private final JwtTokenProvider tokenProvider;
    
    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        Authentication auth = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.username(), 
                request.password()
            )
        );
        
        String token = tokenProvider.generateToken(auth);
        return new TokenResponse(token);
    }
}
```

### Tarde (2 horas)

#### 4. Integrações com OpenFeign (1h)

**Configuração**
```java
@Configuration
@EnableFeignClients
public class FeignConfig {
    
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
```

**Client declarativo**
```java
@FeignClient(
    name = "payment-service",
    url = "${services.payment.url}",
    configuration = PaymentClientConfig.class
)
public interface PaymentClient {
    
    @PostMapping("/api/payments")
    PaymentResponse processPayment(@RequestBody PaymentRequest request);
    
    @GetMapping("/api/payments/{id}")
    PaymentResponse getPayment(@PathVariable String id);
    
    @PostMapping("/api/payments/{id}/refund")
    void refundPayment(@PathVariable String id);
}
```

**Tratamento de erros**
```java
@Component
public class CustomErrorDecoder implements ErrorDecoder {
    
    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 400 -> new BadRequestException("Invalid payment data");
            case 404 -> new PaymentNotFoundException("Payment not found");
            case 503 -> new ServiceUnavailableException("Payment service unavailable");
            default -> new Exception("Generic error");
        };
    }
}
```

#### 5. RestTemplate vs WebClient (30min)

**RestTemplate (síncrono)**
```java
@Service
public class LegacyIntegrationService {
    
    private final RestTemplate restTemplate;
    
    public ProductDto getProduct(Long id) {
        String url = "https://external-api/products/{id}";
        return restTemplate.getForObject(url, ProductDto.class, id);
    }
    
    public ProductDto createProduct(ProductDto product) {
        String url = "https://external-api/products";
        return restTemplate.postForObject(url, product, ProductDto.class);
    }
}
```

**WebClient (reativo)**
```java
@Service
public class ModernIntegrationService {
    
    private final WebClient webClient;
    
    public Mono<ProductDto> getProduct(Long id) {
        return webClient.get()
            .uri("/products/{id}", id)
            .retrieve()
            .onStatus(HttpStatus::is4xxClientError, 
                response -> Mono.error(new NotFoundException()))
            .bodyToMono(ProductDto.class);
    }
}
```

#### 6. Tratamento de Erros e Validações (30min)

**Validação com Bean Validation**
```java
public record CreateOrderRequest(
    @NotNull(message = "Customer ID is required")
    Long customerId,
    
    @NotEmpty(message = "Order must have at least one item")
    @Valid
    List<OrderItemRequest> items,
    
    @NotNull
    @Pattern(regexp = "CREDIT_CARD|PIX|BOLETO")
    String paymentMethod
) {}

public record OrderItemRequest(
    @NotNull
    @Positive
    Long productId,
    
    @Min(value = 1, message = "Quantity must be at least 1")
    @Max(value = 100, message = "Quantity cannot exceed 100")
    Integer quantity
) {}
```

**Custom Validators**
```java
@Constraint(validatedBy = CpfValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCpf {
    String message() default "Invalid CPF";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}

public class CpfValidator implements ConstraintValidator<ValidCpf, String> {
    @Override
    public boolean isValid(String cpf, ConstraintValidatorContext context) {
        // Lógica de validação de CPF
        return cpf != null && cpf.matches("\\d{11}");
    }
}
```

## 💻 Exercícios Práticos

### Exercício 1: Contract-First API (1.5h)

Crie um contrato OpenAPI completo para **API de Pedidos**:

**Endpoints necessários**:
```
POST   /api/orders              - Criar pedido
GET    /api/orders/{id}         - Buscar pedido
GET    /api/orders              - Listar pedidos (paginado)
PATCH  /api/orders/{id}/status  - Atualizar status
DELETE /api/orders/{id}         - Cancelar pedido
GET    /api/orders/customer/{customerId} - Pedidos do cliente
```

**Schemas**:
- Order
- OrderItem
- Customer
- OrderStatus (enum)

Depois, gere o código usando o plugin OpenAPI Generator.

### Exercício 2: Integração com serviço externo (1h)

Implemente integração com um serviço de CEP usando OpenFeign:

```java
@FeignClient(name = "viacep", url = "https://viacep.com.br/ws")
public interface ViaCepClient {
    @GetMapping("/{cep}/json")
    AddressResponse getAddress(@PathVariable String cep);
}
```

Use essa integração em um endpoint que recebe CEP e retorna endereço completo.

### Exercício 3: Segurança (1h)

Implemente autenticação JWT na sua API:

1. Endpoint de login que gera token
2. Proteção de endpoints por roles
3. Extração de informações do usuário do token
4. Refresh token

## 📚 Material de Estudo

### Leitura Obrigatória
- [OpenAPI Specification](https://swagger.io/specification/)
- [REST API Best Practices](https://www.baeldung.com/rest-api-best-practices)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/index.html)
- [OpenFeign Documentation](https://docs.spring.io/spring-cloud-openfeign/docs/current/reference/html/)

### Leitura Complementar
- [Richardson Maturity Model](https://martinfowler.com/articles/richardsonMaturityModel.html)
- [API Design Patterns](https://www.manning.com/books/api-design-patterns)

## 🎯 Objetivos de Aprendizagem

Ao final deste dia, você deve ser capaz de:

- ✅ Criar contratos OpenAPI completos
- ✅ Documentar APIs com Swagger/SpringDoc
- ✅ Aplicar REST best practices
- ✅ Implementar autenticação JWT
- ✅ Integrar com serviços externos usando Feign
- ✅ Validar dados de entrada adequadamente

## 🏠 Tarefa de Casa

1. **Expandir API**:
   - Adicionar autenticação em todos endpoints
   - Criar documentação OpenAPI completa
   - Implementar rate limiting

2. **Estudar**:
   - OAuth2 e OpenID Connect
   - API Gateway patterns
   - Circuit Breaker (Resilience4j)

3. **Preparação para Dia 6**:
   - Revisar JUnit básico
   - Entender conceitos de TDD
   - Instalar plugins de coverage (JaCoCo)

## 📝 Notas do Instrutor

```
Pontos de atenção:
- Demonstrar Swagger UI interativo
- Mostrar geração de clientes em diferentes linguagens
- Explicar difference entre Authentication e Authorization
- Demonstrar debugging de chamadas Feign
- Mostrar logs de requisições HTTP
- Enfatizar importância de contratos bem definidos
```

## 🔗 Links Úteis

- [Swagger Editor](https://editor.swagger.io/)
- [Postman](https://www.postman.com/)
- [JWT.io](https://jwt.io/)
- [ViaCEP API](https://viacep.com.br/)
