# Slide 12: Review e Q&A

**Horário:** 16:30 - 17:00

---

## 📊 Mapa Mental Completo do Dia 4

```mermaid
mindmap
  root((Dia 4<br/>Estratégias<br/>de Testes))
    Pirâmide de Testes
      Unitário — 70% — rápido e isolado
      Integração — 20% — banco real
      E2E — 10% — sistema completo
      Anti-patterns
        Ice Cream Cone
        Hourglass
    JUnit 5
      Padrão AAA — Arrange Act Assert
      @Test @DisplayName @Nested
      @BeforeEach @AfterEach
      @ParameterizedTest
        @CsvSource
        @ValueSource
        @EnumSource
        @MethodSource
      Assertions
        assertEquals assertThrows
        assertAll
        AssertJ — fluente
    Mockito
      @Mock — dependência falsa
      @InjectMocks — injeta mocks
      when thenReturn — programa resposta
      verify — confirma chamada
        times never atLeastOnce
      ArgumentCaptor — captura argumento
        @Captor — como campo
      @Spy — comportamento real parcial
      Matchers — any anyLong eq
    Testcontainers
      PostgreSQL real em Docker
      @Container @DynamicPropertySource
      AbstractIntegrationTest — classe base
      deleteAll no BeforeEach — isolamento
      saveAndFlush — forçar SQL
      Separar *Test vs *IT
    Data Builders
      Defaults sensatos
      Métodos with fluentes
      Foco no que importa no teste
      Um builder por Entity ou DTO
    Qualidade de Código
      JaCoCo — cobertura
        prepare-agent + report
        target/site/jacoco/index.html
      SonarQube — análise estática
        Bugs, Vulnerabilidades, Code Smells
        Quality Gate — critério mínimo
        Docker: sonarqube:lts-community
        mvn sonar:sonar
      SonarLint — no VS Code
        Análise em tempo real
        Conecta com SonarQube Server
```

---

## 🗺️ Jornada Visual do Dia

```mermaid
flowchart LR
    subgraph "Manhã (09:00-12:00)"
        S1["09:00<br/>Abertura<br/>Recap Dia 3"]
        S2["09:20<br/>Pirâmide<br/>de Testes"]
        S3["09:50<br/>JUnit 5<br/>Anatomia AAA"]
        S4["10:30<br/>@ParameterizedTest<br/>+ AssertJ"]
        S5["11:00<br/>Mockito<br/>@Mock @InjectMocks"]
        S6["11:30<br/>Mockito<br/>Captor, verify, Spy"]
    end

    subgraph "Tarde (13:00-17:00)"
        S7["13:00<br/>Testcontainers<br/>Banco Real"]
        S8["13:20<br/>Data Builders<br/>Massa Legível"]
        S9["13:40<br/>Walkthrough<br/>04-testing-demo"]
        S10["14:10<br/>Exercício<br/>TODOs 1-4"]
        S11["15:30<br/>Exercício<br/>TODOs 5-7"]
        S12["16:30<br/>Review<br/>Q&A"]
    end

    S1 --> S2 --> S3 --> S4 --> S5 --> S6
    S7 --> S8 --> S9 --> S10 --> S11 --> S12

    style S1 fill:#dfe6e9
    style S2 fill:#feca57,color:#333
    style S3 fill:#54a0ff,color:#fff
    style S4 fill:#54a0ff,color:#fff
    style S5 fill:#ff6b6b,color:#fff
    style S6 fill:#ff6b6b,color:#fff
    style S7 fill:#1dd1a1,color:#fff
    style S8 fill:#feca57,color:#333
    style S9 fill:#dfe6e9
    style S10 fill:#54a0ff,color:#fff
    style S11 fill:#1dd1a1,color:#fff
    style S12 fill:#dfe6e9
```

---

## 📋 Resumo por Tema

### Testes Unitários — Mockito

```mermaid
flowchart TD
    subgraph "Resumo — Testes Unitários"
        M["@Mock<br/>Cria dependência falsa"]
        I["@InjectMocks<br/>Injeta mocks no objeto real"]
        W["when().thenReturn()<br/>Programa resposta do mock"]
        V["verify()<br/>Confirma que chamou o método"]
        C["ArgumentCaptor<br/>Captura argumento passado"]
        AT["assertThrows<br/>Verifica exceção esperada"]
        AA["assertAll<br/>Múltiplos asserts agrupados"]
        P["@ParameterizedTest<br/>Múltiplos cenários"]
    end

    M --> I --> W --> V --> C
    AT --> AA --> P

    style M fill:#54a0ff,color:#fff
    style C fill:#feca57,color:#333
    style P fill:#1dd1a1,color:#fff
```

| Conceito | Para que serve | Exemplo |
|----------|---------------|---------|
| `@Mock` | Criar dependência falsa | `@Mock ProductRepository` |
| `@InjectMocks` | Injetar mocks no objeto testado | `@InjectMocks ProductService` |
| `when().thenReturn()` | Programar resposta do mock | `when(repo.findById(1L)).thenReturn(...)` |
| `verify()` | Confirmar que chamou o método | `verify(repo).save(any())` |
| `verify(never())` | Confirmar que NÃO chamou | `verify(repo, never()).delete(any())` |
| `ArgumentCaptor` | Capturar argumento passado | `verify(repo).save(captor.capture())` |
| `@Captor` | Captor como campo de classe | Reutilizar em vários testes |
| `@Spy` | Manter comportamento real | Somente quando necessário (~5%) |
| `assertThrows` | Verificar exceção | `assertThrows(NotFoundException.class, ...)` |
| `assertAll` | Verificar múltiplos asserts | `assertAll(() -> ..., () -> ...)` |
| `@ParameterizedTest` | Múltiplos cenários | `@CsvSource({"a,b", "c,d"})` |
| `AssertJ` | Assertions fluentes | `assertThat(x).isEqualTo(y)` |

### Testes de Integração — Testcontainers

| Conceito | Para que serve | Exemplo |
|----------|---------------|---------|
| `@SpringBootTest` | Subir contexto Spring | Classe de teste |
| `@Testcontainers` | Gerenciar containers | Classe de teste |
| `@Container` | Declarar container | `PostgreSQLContainer` |
| `@DynamicPropertySource` | Injetar config do container | `registry.add("url", ...)` |
| `AbstractIntegrationTest` | Reutilizar config | Classe base abstrata |
| `deleteAll()` no `@BeforeEach` | Isolamento entre testes | Limpar dados |
| `saveAndFlush()` | Forçar SQL imediato | Testar constraints UNIQUE |
| Sufixo `IT` | Convenção Maven | `EmployeeRepositoryIT` |

### Data Builders

| Conceito | Para que serve | Exemplo |
|----------|---------------|---------|
| Defaults sensatos | `.build()` gera objeto válido | `name="João"`, `salary=3000` |
| Métodos `with()` | Alterar apenas o relevante | `.withSalary(1000)` |
| `return this` | Encadeamento fluente | `.withName("X").withEmail("Y")` |
| `anEmployee()` | Entrada fluente | `EmployeeBuilder.anEmployee()` |
| Pasta `test/builder/` | Organização | Não é código de produção |

---

## 🧠 Quiz Final

### Pergunta 1: @Mock vs @InjectMocks
**Qual a diferença entre `@Mock` e `@InjectMocks`?**

<details>
<summary>Resposta</summary>

- `@Mock` cria uma implementação **falsa** de uma dependência (retorna null/0/false por padrão)
- `@InjectMocks` cria uma instância **real** da classe testada e injeta os mocks nela

```mermaid
flowchart LR
    M["@Mock<br/>ProductRepository<br/>(FALSO)"] -->|"injeta em"| I["@InjectMocks<br/>ProductService<br/>(REAL)"]

    style M fill:#ff6b6b,color:#fff
    style I fill:#1dd1a1,color:#fff
```
</details>

### Pergunta 2: Unitário vs Integração
**Quando usar teste unitário vs teste de integração?**

<details>
<summary>Resposta</summary>

- **Unitário**: para lógica de negócio pura (Service, Mapper, Validator) — isolado com mocks, sem Docker
- **Integração**: quando depende de recurso externo (banco, API, fila) — teste com o recurso real via Testcontainers

```mermaid
flowchart TD
    Q["O que estou testando?"]
    Q -->|"Lógica de negócio<br/>(validação, cálculo)"| U["Teste Unitário<br/>Mockito"]
    Q -->|"Interação com banco<br/>(query, constraint)"| I["Teste de Integração<br/>Testcontainers"]

    style U fill:#54a0ff,color:#fff
    style I fill:#1dd1a1,color:#fff
```
</details>

### Pergunta 3: saveAndFlush()
**Por que `saveAndFlush()` em vez de `save()` ao testar constraints?**

<details>
<summary>Resposta</summary>

`save()` pode apenas armazenar em cache (1st level cache do Hibernate) sem executar o SQL.

`saveAndFlush()` força a execução imediata do `INSERT`, fazendo o PostgreSQL validar a constraint UNIQUE naquele momento.
</details>

### Pergunta 4: ArgumentCaptor
**Para que serve o `ArgumentCaptor`?**

<details>
<summary>Resposta</summary>

Para capturar o argumento que foi passado a um método mockado. Útil quando o Service cria um objeto internamente e passa ao Repository — o captor permite inspecionar **exatamente** o que foi salvo.

```java
verify(repo).save(captor.capture());
Employee captured = captor.getValue();
assertThat(captured.getName()).isEqualTo("João");
```
</details>

### Pergunta 5: Data Builders
**O que são Data Builders e por que usá-los?**

<details>
<summary>Resposta</summary>

São classes que implementam o Builder Pattern para criar objetos de teste com **defaults sensatos**. Benefícios:
1. **Legibilidade** — cada campo é nomeado
2. **Foco** — só altera o dado relevante para aquele teste
3. **Menos repetição** — defaults cobrem dados comuns
4. **Menos fragilidade** — novo campo na Entity não quebra testes existentes
</details>

### Pergunta 6: TODO 4 vs TODO 7
**Qual a diferença entre testar email duplicado no Service (TODO 4) vs no Banco (TODO 7)?**

<details>
<summary>Resposta</summary>

| Aspecto | TODO 4 (Service) | TODO 7 (Banco) |
|---------|-------------------|----------------|
| Quem valida? | Lógica Java (`if existsByEmail`) | Constraint PostgreSQL (`UNIQUE`) |
| Tipo | Teste unitário (Mockito) | Teste de integração (Testcontainers) |
| Exceção | `DuplicateEmailException` | `DataIntegrityViolationException` |
| Proteção | Primeira camada | Segunda camada (rede de segurança) |

**Ambos são necessários** — camadas diferentes de proteção!
</details>

---

## 📝 Reflexões — Perguntas Frequentes

```mermaid
flowchart TD
    subgraph "Perguntas & Respostas"
        Q1["Qual a cobertura ideal?"]
        A1["80% no Service é bom.<br/>100% geralmente não<br/>vale o esforço."]
        
        Q2["TDD vale a pena?"]
        A2["Para regras complexas, sim.<br/>Para CRUD simples,<br/>escrever depois é OK."]
        
        Q3["Testcontainers é lento?"]
        A3["1ª vez: download da imagem.<br/>Depois: container sobe<br/>em ~2-3 segundos."]
        
        Q4["Devo testar Controller?"]
        A4["Com @WebMvcTest se tiver<br/>lógica complexa. O teste<br/>do Service já cobre muito."]
    end

    Q1 --- A1
    Q2 --- A2
    Q3 --- A3
    Q4 --- A4

    style Q1 fill:#54a0ff,color:#fff
    style Q2 fill:#54a0ff,color:#fff
    style Q3 fill:#54a0ff,color:#fff
    style Q4 fill:#54a0ff,color:#fff
    style A1 fill:#1dd1a1,color:#fff
    style A2 fill:#1dd1a1,color:#fff
    style A3 fill:#1dd1a1,color:#fff
    style A4 fill:#1dd1a1,color:#fff
```

| Pergunta | Resposta |
|----------|----------|
| **Qual a cobertura ideal?** | 80% no Service é um bom alvo. 100% geralmente não vale o esforço de manter. |
| **TDD vale a pena?** | Para regras de negócio complexas, sim. Para CRUD simples, escrever depois é OK. |
| **Quando Testcontainers é lento?** | Na primeira execução (download da imagem Docker). Depois, o container sobe em ~2-3s. |
| **Devo testar Controller?** | Com `@WebMvcTest` (teste de integração leve) se tiver lógica de roteamento/validação complexa. |
| **AssertJ ou JUnit assertions?** | AssertJ é mais expressivo e legível. Prefira sempre que possível. |
| **Quantos testes por método?** | Pelo menos 1 para o caminho feliz + 1 para cada cenário de erro. |

---

## ✅ Checklist de Entrega — 04-employee-api-tests

```mermaid
flowchart TD
    subgraph "Entrega Final"
        C1["✅ EmployeeBuilder com defaults<br/>e métodos with()"]
        C2["✅ EmployeeServiceTest<br/>mínimo 4 testes unitários"]
        C3["✅ @ParameterizedTest<br/>para CPF inválido"]
        C4["✅ EmployeeRepositoryIT<br/>mínimo 3 testes integração"]
        C5["✅ Constraint UNIQUE<br/>no email"]
        C6["✅ mvn test<br/>BUILD SUCCESS"]
    end

    C1 --> C2 --> C3 --> C4 --> C5 --> C6

    style C1 fill:#1dd1a1,color:#fff
    style C2 fill:#1dd1a1,color:#fff
    style C3 fill:#1dd1a1,color:#fff
    style C4 fill:#1dd1a1,color:#fff
    style C5 fill:#1dd1a1,color:#fff
    style C6 fill:#1dd1a1,color:#fff
```

- [ ] `EmployeeBuilder` com defaults sensatos e métodos `with()`
- [ ] `EmployeeServiceTest` com mínimo **4 testes unitários** passando
- [ ] `@ParameterizedTest` para CPF inválido
- [ ] `EmployeeRepositoryIT` com mínimo **3 testes de integração** passando
- [ ] Teste de constraint UNIQUE no email
- [ ] `mvn test` → **BUILD SUCCESS** ✅

---

## 📅 Preparação para o Dia 5

### Temas do Dia 5: Comunicação entre Sistemas e Segurança

```mermaid
mindmap
  root((Dia 5<br/>Comunicação<br/>e Segurança))
    Feign Client
      REST Client declarativo
      Interface Java → HTTP
      Consumir APIs externas
    Resilience4j
      Retry — tentar novamente
      Circuit Breaker — cortar fora
      Fallback — plano B
    CORS
      Cross-Origin Resource Sharing
      Browser bloqueia por padrão
      Configurar no Spring
    JWT
      JSON Web Token
      Header.Payload.Signature
      Autenticação stateless
    Spring Security
      SecurityFilterChain
      Rotas públicas vs protegidas
      Filtro JWT
    OpenAPI Swagger
      Documentação automática
      SpringDoc
      Teste de endpoints
```

### O que preparar para amanhã

- [ ] Docker rodando (`docker run hello-world`)
- [ ] Projeto `04-employee-api-tests` com todos os testes passando
- [ ] Revisar conceito de **HTTP Headers** (`Authorization`, `Content-Type`)
- [ ] Entender o que é uma **API REST** que consome outra API
- [ ] _(Opcional)_ Ler sobre JWT em jwt.io

```mermaid
flowchart LR
    subgraph "Fluxo do Dia 5"
        A["API A<br/>(sua)"] -->|"Feign Client<br/>HTTP"| B["API B<br/>(externa)"]
        B -->|"falha?"| C["Resilience4j<br/>Retry/CircuitBreaker"]
        
        U["Usuário<br/>Browser"] -->|"Authorization: Bearer JWT"| A
        A -->|"SecurityFilterChain<br/>valida JWT"| V{"Token<br/>válido?"}
        V -->|"Sim"| OK["200 OK ✅"]
        V -->|"Não"| DENY["401 Unauthorized ❌"]
    end

    style A fill:#54a0ff,color:#fff
    style B fill:#feca57,color:#333
    style C fill:#ff6b6b,color:#fff
    style OK fill:#1dd1a1,color:#fff
    style DENY fill:#ff6b6b,color:#fff
```

---

## 📚 Referências para Estudo

| Recurso | Link |
|---------|------|
| JUnit 5 User Guide | https://junit.org/junit5/docs/current/user-guide/ |
| Mockito — Getting Started | https://javadoc.io/doc/org.mockito/mockito-core/latest/ |
| Testcontainers — Quick Start | https://java.testcontainers.org/quickstart/junit_5_quickstart/ |
| AssertJ — Core Features | https://assertj.github.io/doc/ |
| Baeldung — Mockito Tutorial | https://www.baeldung.com/mockito-series |
| Baeldung — Testcontainers | https://www.baeldung.com/spring-boot-testcontainers |
| Baeldung — JUnit 5 Parameterized | https://www.baeldung.com/parameterized-tests-junit-5 |
| Baeldung — ArgumentCaptor | https://www.baeldung.com/mockito-argumentcaptor |
| Baeldung — Builder Pattern | https://www.baeldung.com/creational-design-patterns |
| JaCoCo — Java Code Coverage | https://www.jacoco.org/jacoco/ |
| SonarQube — Documentação Oficial | https://docs.sonarsource.com/sonarqube/latest/ |
| SonarLint — VS Code Extension | https://marketplace.visualstudio.com/items?itemName=SonarSource.sonarlint-vscode |
| Baeldung — SonarQube + Maven | https://www.baeldung.com/sonar-qube |

---

## 💡 Dica do Instrutor

> Encerre o dia pedindo para cada aluno compartilhar **uma coisa que aprendeu** que vai mudar a forma como escreve testes. Isso reforça o aprendizado e cria sensação de progresso.

> **Mensagem final**: "Testes não são documentação do que o código faz — são **garantias** do que o código **continua** fazendo. Quando você muda algo e os testes passam, você tem confiança. Quando não tem testes, você tem medo."
