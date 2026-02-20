# Treinamento Java Intermediário

## 📋 Informações Gerais

- **Público-alvo**: Estagiários que já concluíram Java Básico
- **Objetivo**: Capacitar desenvolvedores para nível Pleno Back-end Java
- **Duração**: 9 dias (5 horas/dia) - Total: 45 horas
- **Formato**: Teórico-prático com exercícios e projeto integrador

## 🎯 Objetivos do Curso

Ao final deste treinamento, o aluno será capaz de:

- Desenvolver APIs e microsserviços profissionais com Java 17/21 e Spring Boot
- Aplicar Clean Code, arquiteturas em camadas e Hexagonal em projetos reais
- Implementar testes automatizados com Testcontainers e bancos reais
- Trabalhar com Docker, observabilidade nativa (Actuator, logs estruturados) e entender conceitos de CI/CD
- Consumir e documentar APIs com OpenAPI (Swagger) e Feign Client
- Integrar serviços com bancos SQL, cache (Redis) e mensageria (Kafka/RabbitMQ)
- Realizar Code Review profissional e trabalhar com boas práticas de consultoria

## 📚 Estrutura do Treinamento

### [Dia 1 - Fundamentos Java Moderno e Spring Boot](dia-01/README.md)
- Java 17/21: Novidades e recursos modernos
- Introdução ao Spring Boot 3.x
- Injeção de Dependências e IoC
- Primeira API REST com Servlet

### [Dia 2 - Spring Framework Core](dia-02/README.md)
- Spring Web: Controllers, RestControllers, Request/Response
- Spring Data JPA: Repositories, Entities, Relacionamentos
- Paginação e Ordenação (`Pageable`, `Sort`, `Page<T>`)
- Configuração de banco de dados (H2, PostgreSQL)
- DTOs e Mapeamento (MapStruct/ModelMapper)

### [Dia 3 - Arquitetura, Clean Code e Padronização](dia-03/README.md)
> **Foco**: Sair do "código que funciona" para o "código que escala".

#### 📖 Guia Conceitual

1. **Clean Code — Escrevendo Código Profissional**
   - Nomenclatura significativa: variáveis, métodos e classes que explicam o "porquê", não o "como"
   - Métodos pequenos e coesos: uma função faz uma coisa — máximo ~20 linhas
   - A Regra do Escoteiro: "Deixe o código melhor do que encontrou"
   - Code Smells comuns: God Class, Long Method, Feature Envy, Primitive Obsession
   - Princípio DRY (Don't Repeat Yourself) vs. WET (Write Everything Twice)

2. **Arquitetura em Camadas — O Padrão das Consultorias**
   - Fluxo `Controller → Service → Repository`: quem valida, quem aplica regras, quem acessa o banco
   - Por que nunca expor a Entity JPA no Controller: acoplamento, segurança e evolução de API
   - DTOs (Data Transfer Objects): objetos de entrada/saída independentes da entidade de persistência
   - Mapeamento Entity ↔ DTO: manual, com `ModelMapper` ou `MapStruct`

3. **Introdução à Arquitetura Hexagonal (Ports & Adapters)**
   - O problema do "Service que faz tudo": mistura de regras de negócio com infraestrutura
   - Conceito de Ports (interfaces do domínio) e Adapters (implementações tecnológicas)
   - Estrutura de pacotes: `domain/`, `adapter/in/web/`, `adapter/out/persistence/`
   - Quando vale Hexagonal vs. quando camadas simples já resolvem

4. **Tratamento de Erros Global**
   - `@ControllerAdvice` + `@ExceptionHandler`: centralizar erros em um único lugar
   - Problem Details (RFC 7807): resposta padronizada com `type`, `title`, `status`, `detail`
   - Custom Exceptions de negócio: `ProductNotFoundException`, `InsufficientStockException`

5. **Validação de Dados**
   - Bean Validation com `@Valid`: `@NotNull`, `@NotBlank`, `@Size`, `@Email`, `@Positive`
   - Custom Validators: criando anotações como `@ValidCpf`, `@UniqueEmail`
   - Validação em cascata com `@Valid` em objetos aninhados (DTOs compostos)

#### 📦 Projeto Exemplo: `03-clean-architecture-demo`
> API de Catálogo de Produtos — pronta e funcionando. O aluno roda e acompanha a explicação.

Projeto completo demonstrando todos os conceitos do dia:
- Arquitetura em camadas: `ProductController` → `ProductService` → `ProductRepository`
- Versão refatorada com pacotes hexagonais (`domain/`, `adapter/in/web/`, `adapter/out/persistence/`)
- DTOs com `ProductRequest` / `ProductResponse` e mapeamento via `ProductMapper`
- `GlobalExceptionHandler` retornando Problem Details (RFC 7807)
- Custom exceptions: `ProductNotFoundException`, `DuplicateSkuException`
- Validadores: `@Valid` nos DTOs + custom validator `@ValidSku`
- CRUD completo com endpoints REST documentados

#### ✏️ Projeto Exercício: `03-employee-api`
> API de Gestão de Funcionários — o aluno recebe a estrutura base e implementa os TODOs.

**O que já vem pronto:** entidade `Employee`, `EmployeeRepository`, `application.yml` configurado, dependências no `pom.xml` e um `EmployeeController` básico retornando a entity diretamente.

**TODOs:**
- `// TODO 1: Criar EmployeeRequest e EmployeeResponse (DTOs) para entrada e saída da API`
- `// TODO 2: Criar EmployeeMapper com métodos toEntity() e toResponse()`
- `// TODO 3: Implementar EmployeeService com regras de negócio:`
  - `//   - Salário não pode ser menor que 1412.00`
  - `//   - Email deve ser único (verificar antes de salvar)`
  - `//   - Nome deve ter pelo menos 3 caracteres`
- `// TODO 4: Criar GlobalExceptionHandler com @ControllerAdvice`
  - `//   - Tratar MethodArgumentNotValidException (erros de @Valid)`
  - `//   - Tratar EmployeeNotFoundException (404)`
  - `//   - Tratar DuplicateEmailException (409)`
  - `//   - Retornar respostas no formato Problem Details (RFC 7807)`
- `// TODO 5: Adicionar Bean Validation nos DTOs:`
  - `//   - @NotBlank no nome, @Email no email, @Positive no salário`
- `// TODO 6: Criar custom validator @ValidCpf que valida formato e dígitos`
- `// TODO 7: Refatorar pacotes para estrutura hexagonal:`
  - `//   - Mover regras de negócio para domain/`
  - `//   - Controller e DTOs para adapter/in/web/`
  - `//   - Repository e Entity JPA para adapter/out/persistence/`

---

### [Dia 4 - Estratégias de Testes e Qualidade](dia-04/README.md)
> **Foco**: Confiança para colocar em produção.

#### 📖 Guia Conceitual

1. **Pirâmide de Testes**
   - Unitário (base): rápido, isolado, testa uma classe — custo baixo
   - Integração (meio): testa interação com banco/API real — custo médio
   - E2E (topo): testa o sistema inteiro — custo alto, mais lento
   - Proporção ideal: muitos unitários, alguns de integração, poucos E2E
   - O que cada camada garante e como decidir onde investir

2. **JUnit 5 — Anatomia de um Teste**
   - Estrutura AAA: Arrange (preparar), Act (executar), Assert (verificar)
   - Anotações essenciais: `@Test`, `@DisplayName`, `@BeforeEach`, `@AfterEach`
   - `@ParameterizedTest` + `@CsvSource`: testando múltiplos cenários em uma única estrutura
   - Assertions: `assertEquals`, `assertThrows`, `assertAll`, `assertThat` (AssertJ)

3. **Mockito — Isolando Dependências**
   - `@Mock`: criando implementações falsas de dependências (Repository, Client)
   - `@InjectMocks`: injetando os mocks automaticamente no objeto testado
   - `@Spy`: observando comportamento real parcialmente (útil em refatorações)
   - `when(...).thenReturn(...)`: definindo respostas para cenários controlados
   - `ArgumentCaptor`: capturando exatamente o que foi passado ao repository
   - `verify(...)`: confirmando que um método foi chamado com os parâmetros esperados

4. **Testcontainers — Testes de Integração com Banco Real**
   - Por que não usar H2: diferenças de SQL, tipos de dados e comportamento de constraints
   - `@Container` + `PostgreSQLContainer`: PostgreSQL real subindo em segundos
   - `@DynamicPropertySource`: injetando credenciais do container no Spring em tempo de teste
   - `AbstractIntegrationTest`: classe base reutilizável para todos os testes de integração

5. **Data Builders — Massa de Dados Legível**
   - O problema: `new Employee(1L, "João", "joao@email.com", ..., ..., ...)`
   - Builder Pattern para testes: `EmployeeBuilder.anEmployee().withName("João").build()`
   - Valores default sensatos: builder já vem com dados válidos, aluno altera só o necessário
   - Fixtures com `@BeforeEach`: preparando estado sem repetição

#### 📦 Projeto Exemplo: `04-testing-demo`
> Testes unitários e de integração para a API de Produtos — tudo pronto e passando.

Projeto completo demonstrando todos os conceitos do dia:
- `ProductServiceTest`: testes unitários com `@Mock`, `@InjectMocks`, `ArgumentCaptor`
- `ProductServiceParameterizedTest`: `@ParameterizedTest` com `@CsvSource` para validações
- `ProductRepositoryIT`: testes de integração com Testcontainers (PostgreSQL real)
- `ProductBuilder`: builder fluente para criação de dados de teste
- `AbstractIntegrationTest`: classe base com `PostgreSQLContainer` configurado
- Cobertura >80% na camada Service

#### ✏️ Projeto Exercício: `04-employee-api-tests`
> Adicionar testes completos à `03-employee-api` do dia anterior.

**O que já vem pronto:** dependências de teste (`spring-boot-starter-test`, `testcontainers`), `AbstractIntegrationTest` com `PostgreSQLContainer` configurado, classes de teste vazias (`EmployeeServiceTest`, `EmployeeRepositoryIT`).

**TODOs:**
- `// TODO 1: Implementar EmployeeBuilder com builder fluente:`
  - `//   - Valores default: nome="João Silva", email="joao@email.com", salario=3000.00`
  - `//   - Métodos: withName(), withEmail(), withSalary(), withCpf(), build()`
- `// TODO 2: Em EmployeeServiceTest — testar criação com sucesso:`
  - `//   - Arranjar: mock do repository retornando empty no findByEmail`
  - `//   - Atuar: chamar service.create(request)`
  - `//   - Verificar: capturar argumento salvo no repository com ArgumentCaptor`
- `// TODO 3: Testar regra de salário mínimo:`
  - `//   - Esperar exceção InvalidSalaryException quando salário < 1412.00`
- `// TODO 4: Testar email duplicado:`
  - `//   - Mock findByEmail retornando Optional.of(existingEmployee)`
  - `//   - Esperar DuplicateEmailException`
- `// TODO 5: Criar @ParameterizedTest para CPF inválido:`
  - `//   - @CsvSource com: "123", "00000000000", "1234567890", "abc"`
  - `//   - Esperar InvalidCpfException para cada valor`
- `// TODO 6: Em EmployeeRepositoryIT — testar persistência real:`
  - `//   - Salvar Employee e buscar por ID — comparar campos`
  - `//   - Testar busca paginada (Pageable) com 15 registros`
  - `//   - Testar filtro por departamento contra banco PostgreSQL real`
- `// TODO 7: Testar constraint de email único no banco:`
  - `//   - Salvar dois employees com mesmo email → esperar DataIntegrityViolationException`

---

### [Dia 5 - Comunicação entre Sistemas e Segurança](dia-05/README.md)
> **Foco**: O mundo dos Microsserviços.

#### 📖 Guia Conceitual

1. **Consumo de APIs com Feign Client**
   - Declarativo vs. imperativo: Feign Client vs. `RestTemplate` vs. `WebClient`
   - `@FeignClient`: definindo contratos de comunicação entre serviços como interfaces Java
   - Interceptors: adicionando headers (Authorization, Correlation-ID) a cada chamada
   - Error Decoder: tratando erros do serviço remoto (`404 → NotFoundException`, `500 → fallback`)

2. **Resiliência com Resilience4j**
   - Retry: tentando novamente em caso de falha transitória (quantas vezes, intervalo)
   - Circuit Breaker: estados Closed → Open → Half-Open, quando abrir o circuito
   - Fallback: resposta alternativa quando o serviço externo está indisponível
   - Por que não deixar a cadeia inteira cair quando um serviço fora do ar

3. **CORS (Cross-Origin Resource Sharing)**
   - O problema: por que o browser bloqueia chamadas cross-origin (e o Postman não)
   - Preflight request (OPTIONS): o que o browser envia antes da requisição real
   - Configuração no Spring: `@CrossOrigin` (pontual) vs. `WebMvcConfigurer` (global)
   - Origins, Methods e Headers permitidos

4. **Spring Security + JWT (JSON Web Token)**
   - Autenticação Stateless: login → token JWT → requisições com header `Authorization: Bearer`
   - Estrutura do JWT: Header (alg) + Payload (sub, roles, exp) + Signature
   - `SecurityFilterChain`: definindo rotas públicas (`/auth/**`) e protegidas (`/api/**`)
   - `JwtAuthenticationFilter`: interceptando, extraindo e validando o token
   - `@PreAuthorize("hasRole('ADMIN')")`: controle fino de acesso por método

5. **Documentação com OpenAPI (Swagger)**
   - API First: por que definir o contrato antes de escrever o código
   - SpringDoc: gerando documentação automática a partir do código
   - Anotações: `@Operation`, `@ApiResponse`, `@Schema`, `@Tag`
   - Swagger UI: testando endpoints no browser, autenticação Bearer no "Authorize"

#### 📦 Projeto Exemplo: `05-integration-security-demo`
> Serviço de Pedidos com Feign Client + API de Produtos com JWT e Swagger — tudo integrado e funcionando.

Projeto completo demonstrando todos os conceitos do dia:
- `ProductClient` (Feign): consumo declarativo da API de Produtos
- `CustomErrorDecoder`: tratamento de erros do serviço remoto (404, 500)
- Resilience4j: retry (3 tentativas, 500ms intervalo) + circuit breaker com fallback
- Configuração CORS global via `WebMvcConfigurer` para `localhost:3000`
- `AuthController` com endpoint `POST /auth/login` que retorna JWT
- `JwtAuthenticationFilter`: validação do token em cada requisição
- `SecurityFilterChain`: rotas públicas vs. protegidas por role (ADMIN/USER)
- Swagger UI em `/swagger-ui.html` com botão "Authorize" para Bearer Token

#### ✏️ Projeto Exercício: `05-employee-api-secure`
> Adicionar integração com serviço externo, segurança JWT e documentação à API de Funcionários.

**O que já vem pronto:** dependências no `pom.xml` (Security, OpenFeign, Resilience4j, SpringDoc), classe utilitária `JwtUtil` (geração e validação de token), `SecurityConfig` com `SecurityFilterChain` vazio e `SwaggerConfig` básico.

**TODOs:**
- `// TODO 1: Criar DepartmentClient — interface Feign para consumir GET /api/departments/{id}`
  - `//   - Anotar com @FeignClient(name = "department-service", url = "${department.api.url}")`
- `// TODO 2: Criar CustomErrorDecoder implements ErrorDecoder:`
  - `//   - 404 → DepartmentNotFoundException`
  - `//   - 500 → ExternalServiceException com mensagem amigável`
- `// TODO 3: Configurar Resilience4j no application.yml:`
  - `//   - Retry: maxAttempts=3, waitDuration=500ms`
  - `//   - CircuitBreaker: failureRateThreshold=50, slidingWindowSize=10`
  - `//   - Criar fallback que retorna departamento "Não Disponível"`
- `// TODO 4: Configurar CORS global em WebMvcConfigurer:`
  - `//   - allowedOrigins: "http://localhost:3000"`
  - `//   - allowedMethods: GET, POST, PUT, DELETE`
  - `//   - allowedHeaders: "*"`
- `// TODO 5: Implementar AuthController:`
  - `//   - POST /auth/login recebe LoginRequest (email, password)`
  - `//   - Valida credenciais e retorna TokenResponse com JWT`
- `// TODO 6: Completar SecurityFilterChain em SecurityConfig:`
  - `//   - Rotas públicas: /auth/**, /swagger-ui/**, /v3/api-docs/**`
  - `//   - Rotas protegidas: /api/** (requer JWT)`
  - `//   - Desabilitar CSRF (API stateless)`
- `// TODO 7: Implementar JwtAuthenticationFilter extends OncePerRequestFilter:`
  - `//   - Extrair token do header Authorization (Bearer ...)`
  - `//   - Validar com JwtUtil e setar no SecurityContextHolder`
- `// TODO 8: Documentar endpoints com SpringDoc:`
  - `//   - @Tag(name = "Employees") no controller`
  - `//   - @Operation(summary = "...") em cada endpoint`
  - `//   - @ApiResponse(responseCode = "201", description = "...")`
  - `//   - Testar no Swagger UI com Bearer Token`

---

### [Dia 6 - Persistência Avançada e Mensageria](dia-06/README.md)
> **Foco**: Performance e desacoplamento.

#### 📖 Guia Conceitual

1. **JPA Avançado — Performance de Verdade**
   - Problema N+1: como `findAll()` com `@OneToMany` gera centenas de queries escondidas
   - `JOIN FETCH` na JPQL: `SELECT p FROM Product p JOIN FETCH p.category`
   - `@EntityGraph`: declarando o grafo de eager loading sem poluir a query
   - Projeções DTO: `SELECT new ProductSummary(p.id, p.name, p.price) FROM Product p`
   - Paginação com `Pageable`: nunca retornar listas sem limite (`Page<T>`, `Slice<T>`)

2. **Migrations com Flyway**
   - Por que `ddl-auto: update` é proibido em produção: riscos de perda de dados
   - Convenção de nomes: `V1__create_products.sql`, `V2__add_category_column.sql`
   - Versionamento de esquema: cada alteração é rastreável e reproduzível
   - Integração com Spring Boot: execução automática ao subir a aplicação
   - Rollback: estratégias para reverter uma migration problemática

3. **Mensageria com RabbitMQ**
   - O que é um Message Broker: intermediando comunicação assíncrona entre serviços
   - Quando usar filas: processos demorados, notificações, desacoplamento de módulos
   - RabbitMQ vs. Kafka: filas ponto-a-ponto vs. streaming pub/sub com retenção
   - Producer: `RabbitTemplate.convertAndSend(exchange, routingKey, message)`
   - Consumer: `@RabbitListener(queues = "order-events")` com serialização JSON
   - Exchanges e Routing Keys: direct, topic, fanout

4. **Cache com Redis**
   - Por que cachear: dados consultados frequentemente e que mudam pouco (categorias, configurações)
   - Spring Cache + Redis: abstração `@Cacheable`, `@CachePut`, `@CacheEvict`
   - TTL (Time-to-Live): expiração automática para evitar dados obsoletos
   - Invalidação: quando e como limpar o cache ao alterar dados no banco
   - Armadilhas: cache stampede, dados inconsistentes entre cache e banco

#### 📦 Projeto Exemplo: `06-persistence-messaging-demo`
> API de Pedidos com JPA otimizado, Flyway, RabbitMQ e Redis — tudo rodando via Docker Compose.

Projeto completo demonstrando todos os conceitos do dia:
- Queries propositalmente com N+1 + versão corrigida com `JOIN FETCH` e `@EntityGraph`
- Projeção DTO: `OrderSummary` direto na JPQL (sem carregar entidade completa)
- Paginação: `GET /orders?page=0&size=10&sort=createdAt,desc`
- Migrations Flyway: `V1__create_orders.sql`, `V2__create_order_items.sql`, `V3__add_status_column.sql`
- Producer: publica `OrderCreatedEvent` no RabbitMQ ao criar pedido
- Consumer: escuta o evento e atualiza estoque (simulado com log)
- Cache Redis: `@Cacheable` na listagem de categorias com TTL de 10 minutos
- `docker-compose.yml` com PostgreSQL + RabbitMQ + Redis

#### ✏️ Projeto Exercício: `06-employee-api-advanced`
> Otimizar persistência, adicionar migrations, mensageria e cache à API de Funcionários.

**O que já vem pronto:** `docker-compose.yml` com PostgreSQL + RabbitMQ + Redis, Flyway configurado (sem migrations), `application.yml` com `spring.jpa.show-sql=true` para visualizar o N+1, e queries propositalmente ineficientes no `EmployeeRepository`.

**TODOs:**
- `// TODO 1: Identificar o N+1 no endpoint GET /employees:`
  - `//   - Rodar a aplicação e observar os logs SQL`
  - `//   - Contar quantas queries são geradas para listar 10 funcionários com departamento`
- `// TODO 2: Corrigir o N+1 de duas formas:`
  - `//   - Opção A: JOIN FETCH na JPQL personalizada`
  - `//   - Opção B: @EntityGraph(attributePaths = {"department"}) no método do Repository`
- `// TODO 3: Criar projeção DTO EmployeeSummary:`
  - `//   - Fields: id, name, departmentName (apenas o necessário para listagem)`
  - `//   - Query JPQL: SELECT new EmployeeSummary(e.id, e.name, d.name) FROM Employee e JOIN e.department d`
- `// TODO 4: Adicionar paginação no GET /employees:`
  - `//   - Receber Pageable como parâmetro no controller`
  - `//   - Retornar Page<EmployeeSummary> com metadata (totalPages, totalElements)`
- `// TODO 5: Criar migrations Flyway:`
  - `//   - V1__create_departments.sql (id, name, code)`
  - `//   - V2__create_employees.sql (id, name, email, cpf, salary, department_id FK)`
  - `//   - Remover spring.jpa.hibernate.ddl-auto do application.yml`
- `// TODO 6: Publicar evento EmployeeCreatedEvent no RabbitMQ:`
  - `//   - Criar classe EmployeeCreatedEvent (employeeId, name, email, departmentName)`
  - `//   - Configurar exchange "employee-events", routingKey "employee.created"`
  - `//   - Publicar no EmployeeService.create() após salvar no banco`
- `// TODO 7: Criar Consumer que escuta EmployeeCreatedEvent:`
  - `//   - @RabbitListener(queues = "employee-notifications")`
  - `//   - Logar: "Enviando email de boas-vindas para {name} ({email})"`
- `// TODO 8: Cachear listagem de departamentos com Redis:`
  - `//   - @Cacheable(value = "departments", key = "'all'") no DepartmentService.findAll()`
  - `//   - TTL: 10 minutos (configurar no application.yml)`
  - `//   - @CacheEvict(value = "departments", allEntries = true) ao criar/atualizar departamento`

---

### [Dia 7 - Docker, Cloud Readiness e Observabilidade](dia-07/README.md)
> **Foco**: "Na minha máquina funciona" não é desculpa.

#### 📖 Guia Conceitual

1. **Docker — Containerizando a Aplicação**
   - Dockerfile: camadas, cache de layers, por que a ordem das instruções importa
   - Multi-stage build: stage `build` com Maven/Gradle + stage `runtime` com JRE slim (~80MB)
   - `.dockerignore`: excluindo `target/`, `.git/`, `.idea/` para imagens menores e builds rápidos
   - Variáveis de ambiente: externalizando config (`DB_URL`, `REDIS_HOST`) em vez de hardcoding

2. **Docker Compose — Orquestrando a Stack**
   - `docker-compose.yml`: definindo app + banco + cache + fila em um único arquivo
   - `depends_on` + health checks: garantindo que o banco esteja pronto antes da app subir
   - Volumes: persistindo dados do PostgreSQL entre restarts
   - Networks: isolando comunicação entre containers
   - Um `docker compose up` e tudo funciona

3. **Spring Actuator — Observabilidade Nativa**
   - Endpoints embutidos: `/health` (liveness/readiness), `/metrics`, `/info`, `/env`
   - Métricas JVM: memória heap, threads ativas, GC, uptime
   - Métricas HTTP: requests/s, latência, status codes por endpoint
   - Custom health indicators: verificação de dependências externas
   - Boas práticas: quais endpoints expor em produção vs. desenvolvimento

4. **Logs Estruturados**
   - Por que logs em texto puro não escalam: dificuldade de parsing, busca e correlação
   - Logback + Logstash Encoder: gerando logs em JSON automaticamente
   - MDC (Mapped Diagnostic Context): adicionando `traceId`, `userId`, `requestId` ao log
   - Níveis de log: quando usar `DEBUG`, `INFO`, `WARN`, `ERROR` — boas práticas
   - Correlação de requisições: usando `traceId` para rastrear uma requisição fim-a-fim

5. **Observabilidade em Produção (Conceitual)**
   - Pilares da Observabilidade: Métricas, Logs e Traces — o que cada um resolve
   - Ferramentas de mercado: Prometheus + Grafana, Datadog, ELK Stack, New Relic, Zipkin/Jaeger
   - Distributed Tracing: conceito de `traceId` e `spanId` entre microsserviços
   - Como as empresas montam suas stacks: coleta → armazenamento → visualização → alertas
   - O papel do desenvolvedor: instrumentar a aplicação (Actuator, logs JSON, Micrometer) para que a infra consuma

6. **CI/CD (Conceitual)**
   - O que é Integração Contínua: build e testes automáticos a cada push
   - O que é Entrega/Deploy Contínuo: pipeline automatizado até produção
   - Ferramentas: GitHub Actions, GitLab CI, Jenkins, Azure DevOps
   - Etapas típicas: build → testes unitários → testes de integração → análise estática → deploy
   - Por que CI/CD é obrigatório: "código que não passa no pipeline não vai pra produção"

#### 📦 Projeto Exemplo: `07-docker-actuator-demo`
> App Spring Boot dockerizada com Actuator e logs estruturados — tudo subindo com `docker compose up`.

Projeto completo demonstrando os conceitos práticos do dia:
- `Dockerfile` multi-stage build otimizado (~80MB com JRE slim)
- `.dockerignore` configurado
- `docker-compose.yml` com: app Spring Boot, PostgreSQL, Redis, RabbitMQ
- Health checks em todos os serviços
- Actuator expondo `/health`, `/metrics`, `/info` com métricas customizadas
- Custom Health Indicator verificando conectividade com RabbitMQ
- `logback-spring.xml` com `LogstashEncoder` gerando logs em JSON
- MDC filter adicionando `traceId` e `userId` em cada log
- Profile `dev` (logs texto) vs. `prod` (logs JSON) no Logback

#### ✏️ Projeto Exercício: `07-employee-api-production`
> Dockerizar a API de Funcionários e adicionar observabilidade nativa.

**O que já vem pronto:** `Dockerfile` básico não otimizado (imagem ~400MB), `docker-compose.yml` com apenas PostgreSQL, `logback-spring.xml` com logs em texto puro e dependência do Actuator sem nenhum endpoint exposto.

**TODOs:**
- `// TODO 1: Otimizar o Dockerfile com multi-stage build:`
  - `//   - Stage 1 (build): FROM maven:3.9-eclipse-temurin-21 → copiar pom.xml e src → mvn package`
  - `//   - Stage 2 (runtime): FROM eclipse-temurin:21-jre-alpine → copiar JAR → ENTRYPOINT`
  - `//   - Meta: imagem final < 100MB`
- `// TODO 2: Criar .dockerignore:`
  - `//   - Excluir: target/, .git/, .idea/, *.iml, .env, docker-compose*.yml`
- `// TODO 3: Completar docker-compose.yml:`
  - `//   - Adicionar: app (build: .), Redis, RabbitMQ`
  - `//   - Health checks: pg_isready para PostgreSQL, redis-cli ping para Redis`
  - `//   - Variáveis de ambiente: SPRING_DATASOURCE_URL, SPRING_REDIS_HOST, etc.`
  - `//   - depends_on com condition: service_healthy`
- `// TODO 4: Configurar Actuator no application.yml:`
  - `//   - Expor endpoints: health, metrics, info`
  - `//   - management.endpoints.web.exposure.include=health,metrics,info`
  - `//   - management.endpoint.health.show-details=always`
- `// TODO 5: Criar custom HealthIndicator para RabbitMQ:`
  - `//   - Implementar HealthIndicator interface`
  - `//   - Retornar Health.up() se conexão OK, Health.down() se falhar`
- `// TODO 6: Configurar logs estruturados:`
  - `//   - Substituir texto puro por LogstashEncoder no logback-spring.xml`
  - `//   - Criar profile: <springProfile name="dev"> com texto, <springProfile name="prod"> com JSON`
  - `//   - Criar MdcFilter (OncePerRequestFilter) que adiciona traceId e userId ao MDC`
- `// TODO 7: Adicionar logging contextual nos services:`
  - `//   - log.info("Criando funcionário: {}", employee.getName()) nos métodos principais`
  - `//   - log.error("Erro ao buscar funcionário: {}", id, exception) nos catch`
  - `//   - Usar MDC.put("employeeId", id) para contexto de negócio`

---

### [Dia 8 - Projeto Prático: O Desafio da Consultoria](dia-08/README.md)
> **Hands-on**: O projeto será um **Sistema de Voucher de Compras**.

#### 📖 Guia Conceitual

1. **Dinâmica de Consultoria**
   - User Story com critérios de aceite propositalmente vagos — simulando cliente real
   - Fazer as perguntas certas antes de codar: escopar, negociar e priorizar
   - Definição de "pronto": o que significa "funcionar" para o cliente vs. para o desenvolvedor

2. **Ritos Ágeis**
   - Daily Scrum simulado: o que fiz, o que vou fazer, quais impedimentos
   - Timeboxing: aprender a trabalhar com prazos curtos e entregas incrementais
   - Kanban simplificado: To Do → In Progress → Code Review → Done

3. **Git Profissional**
   - Fork do repositório template → branch por feature → Pull Request
   - Commits semânticos: `feat:`, `fix:`, `refactor:`, `test:`, `docs:`
   - Code Review via PR: checklist de nomenclatura, testes, tratamento de erros, segurança
   - Feedback construtivo: como apontar problemas sem ser ofensivo

#### ✏️ Projeto Exercício: `08-voucher-system`
> Sistema de Voucher de Compras — repositório template no GitHub com estrutura base pronta.

**O que já vem pronto no template:**
- Estrutura de pacotes (camadas ou hexagonal — à escolha do time)
- `docker-compose.yml` com PostgreSQL + Redis + RabbitMQ
- `application.yml` configurado para os containers
- Migrations Flyway iniciais (`V1__create_vouchers.sql`, `V2__create_products.sql`)
- `AbstractIntegrationTest` com Testcontainers
- `README.md` com a User Story e critérios de aceite

**TODOs:**
- `// TODO 1: Implementar VoucherService — criar, validar e resgatar vouchers`
  - `//   - Gerar código único (UUID ou alfanumérico de 8 caracteres)`
  - `//   - Definir data de expiração (padrão: 30 dias)`
- `// TODO 2: Implementar regras de negócio:`
  - `//   - Voucher expirado não pode ser resgatado → VoucherExpiredException`
  - `//   - Voucher já utilizado não pode ser reutilizado → VoucherAlreadyRedeemedException`
  - `//   - Valor mínimo do pedido para aplicar o voucher`
- `// TODO 3: Criar VoucherController com endpoints REST:`
  - `//   - POST /api/vouchers (criar) — protegido por role ADMIN`
  - `//   - GET /api/vouchers/{code} (consultar)`
  - `//   - POST /api/vouchers/{code}/redeem (resgatar)`
  - `//   - GET /api/vouchers?status=ACTIVE&page=0&size=10 (listar com filtro e paginação)`
- `// TODO 4: Adicionar validações com @Valid nos DTOs de entrada`
- `// TODO 5: Implementar GlobalExceptionHandler com Problem Details (RFC 7807)`
- `// TODO 6: Criar testes unitários para VoucherService (mínimo 5 cenários):`
  - `//   - Criar voucher com sucesso`
  - `//   - Resgatar voucher válido`
  - `//   - Resgatar voucher expirado → exceção`
  - `//   - Resgatar voucher já utilizado → exceção`
  - `//   - Consultar voucher inexistente → exceção`
- `// TODO 7: Criar testes de integração com Testcontainers para VoucherRepository`
- `// TODO 8: Publicar evento VoucherRedeemedEvent no RabbitMQ ao resgatar voucher`
- `// TODO 9: Cachear vouchers ativos com Redis (@Cacheable)`
- `// TODO 10: Documentar endpoints com OpenAPI/Swagger`
- `// TODO 11: Proteger endpoints de criação/deleção com JWT (role ADMIN)`

#### 📝 Dinâmica do Dia
- **Manhã**: Recebimento da User Story, perguntas ao "cliente" (instrutor), planejamento e início do desenvolvimento
- **Tarde**: Desenvolvimento ativo, code review entre times via Pull Request, acompanhamento dos instrutores

---

### [Dia 9 - Mentoria, Refatoração e Soft Skills](dia-09/README.md)

#### 📖 Guia Conceitual

1. **Refactoring — Melhorando Código Existente**
   - Code Smells: God Class, Long Method, Feature Envy, Data Clumps
   - Técnicas: Extract Method, Extract Class, Replace Conditional with Polymorphism
   - Refatoração segura: sempre com testes passando — green → refactor → green
   - Antes vs. depois: impacto na legibilidade, testabilidade e manutenção

2. **Soft Skills para Consultoria**
   - Como se destacar nos primeiros meses: proatividade, documentação e comunicação
   - Lidar com diferentes clientes: adaptar linguagem técnica ao nível do interlocutor
   - Especialista vs. generalista: vantagens, desvantagens, quando escolher cada caminho
   - Certificações, comunidades, open source e networking

#### 🔄 Continuação do Projeto: `08-voucher-system`

**Atividades do dia:**
- **Refactoring ao vivo**: instrutores selecionam trechos de código dos alunos (com permissão) e refatoram ao vivo — antes/depois na tela
- **Apresentação Final**: cada time demonstra o `08-voucher-system` (15 min por time — demo ao vivo + decisões técnicas + aprendizados)
- **Feedback dos instrutores e encerramento do treinamento**

## 🚀 Projeto Integrador

No Dia 8, os alunos receberão o desafio de desenvolver um **Sistema de Voucher de Compras**, simulando a dinâmica de uma consultoria real:

- Recebimento de User Story com critérios de aceite
- Arquitetura em camadas / hexagonal
- Testes automatizados com Testcontainers
- Code Review via Pull Requests
- Simulação de Daily Scrum
- Apresentação técnica da solução (Dia 9)

## 📖 Pré-requisitos

- Java Básico concluído
- Git básico
- Conhecimento de POO
- SQL básico
- IDE instalada (IntelliJ IDEA ou VS Code)
- Docker instalado
- Conta no GitHub

## 🛠️ Ferramentas Utilizadas

- Java 17 ou 21
- Spring Boot 3.x
- Maven/Gradle
- PostgreSQL + Testcontainers
- Redis
- RabbitMQ / Kafka
- Docker / Docker Compose
- Flyway
- Git/GitHub
- Postman/Insomnia
- IntelliJ IDEA / VS Code

## 📝 Avaliação

- Exercícios diários: 40%
- Projeto integrador: 40%
- Participação e code review: 20%

## 📚 Material de Apoio

- [Documentação Oficial Spring](https://spring.io/projects/spring-boot)
- [Baeldung Java Tutorials](https://www.baeldung.com/)
- [Microsoft Learn - Java on Azure](https://learn.microsoft.com/azure/developer/java/)

---

**Instrutor**: Deusemar Chaves Junior  
**Instrutor**: Fernando Dias Abrão
