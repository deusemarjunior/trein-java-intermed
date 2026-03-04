# Dia 5 - Comunicação entre Sistemas e Segurança

**Duração**: 5 horas  
**Objetivo**: O mundo dos Microsserviços — consumindo APIs externas com Feign Client, aplicando resiliência com Resilience4j, protegendo endpoints com Spring Security + JWT e documentando com OpenAPI/Swagger

---

## 🎯 Agenda do Dia

| Horário | Duração | Tópico | Tipo |
|---------|---------|--------|------|
| 09:00 - 09:15 | 15min | Recap Dia 4 e Introdução ao Dia 5 | Discussão |
| 09:15 - 09:45 | 30min | Consumo de APIs com Feign Client | Teórico |
| 09:45 - 10:15 | 30min | Resiliência com Resilience4j (Retry, Circuit Breaker) | Teórico |
| 10:15 - 10:45 | 30min | CORS — Cross-Origin Resource Sharing | Teórico (exemplo didático) |
| 10:45 - 11:00 | 15min | ☕ Coffee Break | - |
| 11:00 - 11:30 | 30min | Spring Security + JWT (Autenticação Stateless) | Teórico |
| 11:30 - 12:00 | 30min | Spring Security + JWT (Filter Chain e Autorização) | Teórico |
| 12:00 - 13:00 | 1h | 🍽️ Almoço | - |
| 13:00 - 13:20 | 20min | Documentação com OpenAPI / Swagger | Teórico |
| 13:20 - 13:50 | 30min | Walkthrough `05-integration-security-demo` | Demo |
| 13:50 - 15:30 | 1h40 | Exercício `05-employee-api-secure` (TODOs 1-4) | Hands-on |
| 15:30 - 16:30 | 1h | Exercício `05-employee-api-secure` (TODOs 5-8) | Hands-on |
| 16:30 - 17:00 | 30min | Review, Swagger UI integrado e Q&A | Discussão |

---

## 📦 Material Necessário (Checklist Instrutor)

### Software
- [ ] JDK 21 instalado
- [ ] Maven 3.8+
- [ ] IDE com suporte a Java (IntelliJ ou VS Code)
- [ ] Podman Desktop rodando (para Testcontainers e serviços auxiliares)
- [ ] _(Opcional)_ Postman ou extensão REST Client no VS Code

### Preparação
- [ ] Projeto `05-integration-security-demo` rodando com Swagger UI acessível
- [ ] Projeto `05-employee-api-secure` com TODOs prontos e dependências configuradas
- [ ] Verificar que `podman run docker.io/library/hello-world` funciona

---

## 📋 Conteúdo Programático

---

### 1. Consumo de APIs com Feign Client

No mundo dos microsserviços, serviços precisam se comunicar. O **Feign Client** permite consumir APIs externas de forma **declarativa** — como se fosse uma interface Java comum.

#### Declarativo vs. Imperativo

| Abordagem | Ferramenta | Estilo |
|-----------|-----------|--------|
| Imperativa | `RestTemplate` | Monta URL, headers, body manualmente |
| Imperativa reativa | `WebClient` | Programação reativa (non-blocking) |
| **Declarativa** | **Feign Client** | Interface Java com anotações — o framework faz o resto |

#### Exemplo: Consumindo API de Departamentos

```java
@FeignClient(
    name = "department-service",
    url = "${department.api.url}",
    configuration = FeignConfig.class
)
public interface DepartmentClient {

    @GetMapping("/api/departments/{id}")
    DepartmentResponse findById(@PathVariable Long id);

    @GetMapping("/api/departments")
    List<DepartmentResponse> findAll();
}
```

**Uso no Service:**

```java
@Service
public class EmployeeService {

    private final DepartmentClient departmentClient;

    public EmployeeResponse create(EmployeeRequest request) {
        // Feign cuida de HTTP, serialização, headers...
        DepartmentResponse dept = departmentClient.findById(request.departmentId());
        // ... lógica de negócio
    }
}
```

#### Interceptors — Adicionando Headers Automáticos

```java
@Component
public class FeignAuthInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        template.header("Authorization", "Bearer " + getToken());
        template.header("X-Correlation-ID", UUID.randomUUID().toString());
    }
}
```

#### Error Decoder — Tratando Erros do Serviço Remoto

```java
public class CustomErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        return switch (response.status()) {
            case 404 -> new DepartmentNotFoundException("Departamento não encontrado");
            case 500 -> new ExternalServiceException("Serviço de departamentos indisponível");
            default -> new RuntimeException("Erro inesperado: " + response.status());
        };
    }
}
```

---

### 2. Resiliência com Resilience4j

Quando um serviço externo falha, o que acontece com o seu? Sem resiliência, **uma falha cascateia por todo o sistema**.

#### Retry — Tentando Novamente

```yaml
resilience4j:
  retry:
    instances:
      departmentService:
        maxAttempts: 3
        waitDuration: 500ms
        retryExceptions:
          - java.io.IOException
          - feign.FeignException.ServiceUnavailable
```

```java
@Retry(name = "departmentService", fallbackMethod = "departmentFallback")
public DepartmentResponse getDepartment(Long id) {
    return departmentClient.findById(id);
}
```

#### Circuit Breaker — Protegendo o Sistema

```
Estado CLOSED (normal)
    → falhas atingem threshold
Estado OPEN (rejeitando chamadas)
    → após wait duration
Estado HALF-OPEN (teste)
    → se sucesso → CLOSED
    → se falha → OPEN
```

```yaml
resilience4j:
  circuitbreaker:
    instances:
      departmentService:
        failureRateThreshold: 50
        slidingWindowSize: 10
        waitDurationInOpenState: 10s
        permittedNumberOfCallsInHalfOpenState: 3
```

#### Fallback — Resposta Alternativa

```java
public DepartmentResponse departmentFallback(Long id, Exception ex) {
    return new DepartmentResponse(id, "Não Disponível", "N/A");
}
```

> **Princípio**: Melhor retornar parcial do que derrubar tudo.

---


### 3. CORS (Cross-Origin Resource Sharing)

#### O que é CORS?

CORS é uma política de segurança dos navegadores para controlar requisições entre diferentes origens. Mesmo sem frontend neste treinamento, mostramos a configuração para fins didáticos, pois APIs reais costumam ser consumidas por aplicações web ou mobile.

> **Nota:** Não há frontend React ou Angular neste treinamento. Os exemplos usam origens como `localhost:3000` apenas para ilustrar cenários comuns.

#### Preflight Request (OPTIONS)

Antes da requisição real, o browser envia um `OPTIONS` perguntando:
- Quais origens podem acessar?
- Quais métodos HTTP são permitidos?
- Quais headers são aceitos?

#### Configuração Global no Spring

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("http://localhost:3000") // Exemplo didático, não há frontend neste treinamento
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

#### @CrossOrigin (Pontual)

```java
@CrossOrigin(origins = "http://localhost:3000")
@RestController
@RequestMapping("/api/products")
public class ProductController { ... }
```

---

### 4. Spring Security + JWT (JSON Web Token)

#### Autenticação Stateless

Em APIs REST, não usamos sessão. O fluxo é:

```
1. Cliente envia POST /auth/login (email + password)
2. Servidor valida credenciais
3. Servidor gera JWT e retorna ao cliente
4. Cliente envia JWT no header Authorization em cada request
5. Servidor valida JWT e autoriza o acesso
```

#### Estrutura do JWT

```
Header.Payload.Signature

Header:  { "alg": "HS256", "typ": "JWT" }
Payload: { "sub": "joao@email.com", "roles": ["ADMIN"], "exp": 1700000000 }
Signature: HMACSHA256(base64(header) + "." + base64(payload), secret)
```

#### SecurityFilterChain

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/**").authenticated()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}
```

#### JwtAuthenticationFilter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            if (jwtUtil.isTokenValid(token)) {
                String email = jwtUtil.extractEmail(token);
                List<String> roles = jwtUtil.extractRoles(token);

                var authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();

                var authentication = new UsernamePasswordAuthenticationToken(
                    email, null, authorities
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
```

#### @PreAuthorize — Controle Fino

```java
@PreAuthorize("hasRole('ADMIN')")
@DeleteMapping("/{id}")
public ResponseEntity<Void> delete(@PathVariable Long id) { ... }
```

---

### 5. Documentação com OpenAPI (Swagger)

#### Por que Documentar?

> **API sem documentação é API que ninguém usa** — ou usa errado.

#### SpringDoc — Geração Automática

Adicione a dependência e acesse `/swagger-ui.html`:

```xml
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

#### Anotações

```java
@Tag(name = "Products", description = "Gerenciamento de Produtos")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Operation(summary = "Listar todos os produtos",
               description = "Retorna lista paginada de produtos")
    @ApiResponse(responseCode = "200", description = "Produtos encontrados")
    @GetMapping
    public Page<ProductResponse> findAll(Pageable pageable) { ... }

    @Operation(summary = "Criar novo produto")
    @ApiResponse(responseCode = "201", description = "Produto criado")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "SKU duplicado")
    @PostMapping
    public ResponseEntity<ProductResponse> create(
            @RequestBody @Schema(implementation = ProductRequest.class) ProductRequest request) { ... }
}
```

#### Swagger UI com JWT

```java
@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Employee API")
                .version("1.0")
                .description("API de Gestão de Funcionários"))
            .addSecurityItem(new SecurityRequirement().addList("Bearer Token"))
            .components(new Components()
                .addSecuritySchemes("Bearer Token",
                    new SecurityScheme()
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT")));
    }
}
```

---

## 📦 Projetos do Dia

### `05-integration-security-demo` (Projeto Completo — Demonstração)

> Serviço de Pedidos com Feign Client + API de Produtos com JWT e Swagger.

Projeto completo demonstrando todos os conceitos:
- `ProductClient` (Feign): consumo declarativo da API de Produtos
- `CustomErrorDecoder`: tratamento de erros do serviço remoto
- Resilience4j: retry + circuit breaker com fallback
- Configuração CORS global para `localhost:3000`
- `AuthController` com endpoint `POST /auth/login` retornando JWT
- `JwtAuthenticationFilter`: validação do token em cada requisição
- `SecurityFilterChain`: rotas públicas vs. protegidas por role
- Swagger UI com botão "Authorize" para Bearer Token

**Porta**: `8088`

### `05-employee-api-secure` (Exercício — TODOs 1-8)

> Adicionar integração, segurança JWT e documentação à API de Funcionários.

**TODOs**: 8 tarefas cobrindo Feign Client, Error Decoder, Resilience4j, CORS, AuthController, SecurityFilterChain, JwtAuthenticationFilter e Swagger.

**Porta**: `8089`

---

## 📎 Slides

| Slide | Tópico |
|-------|--------|
| [slide-01](slide-01.md) | Abertura e Recap do Dia 4 |
| [slide-02](slide-02.md) | Consumo de APIs com Feign Client |
| [slide-03](slide-03.md) | Resiliência com Resilience4j |
| [slide-04](slide-04.md) | CORS — Cross-Origin Resource Sharing |
| [slide-05](slide-05.md) | Spring Security + JWT (Autenticação Stateless) |
| [slide-06](slide-06.md) | Spring Security + JWT (Filter Chain e Autorização) |
| [slide-07](slide-07.md) | Documentação com OpenAPI / Swagger |
| [slide-08](slide-08.md) | Walkthrough — 05-integration-security-demo |
| [slide-09](slide-09.md) | Exercício — 05-employee-api-secure (TODOs 1-4) |
| [slide-10](slide-10.md) | Exercício — 05-employee-api-secure (TODOs 5-8) |
| [slide-11](slide-11.md) | Review e Q&A |
