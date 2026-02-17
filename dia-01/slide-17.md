# Slide 17: API First (Contract First) vs Code First

**Tópico complementar**

---

## 🤔 O que vem primeiro: o código ou o contrato?

```mermaid
flowchart LR
    Q{"🤔 Como você<br/>começa uma API?"}
    
    Q -->|"Escrevo o<br/>código Java"| CF["💻 Code First"]
    Q -->|"Defino o contrato<br/>OpenAPI antes"| AF["📝 API First<br/>(Contract First)"]

    style Q fill:#FFF8E1,stroke:#F57F17,font-weight:bold
    style CF fill:#BBDEFB,stroke:#1565C0
    style AF fill:#C8E6C9,stroke:#2E7D32
```

---

## 💻 Abordagem Code First

```mermaid
flowchart TB
    subgraph code_first["💻 CODE FIRST — Código gera o contrato"]
        direction TB
        C1["1️⃣ Escreve o Controller<br/>@RestController + @GetMapping"]
        C2["2️⃣ Escreve os DTOs<br/>Records/Classes Java"]
        C3["3️⃣ Adiciona anotações Swagger<br/>@Operation, @Schema"]
        C4["4️⃣ SpringDoc gera o OpenAPI<br/>automaticamente em /api-docs"]
        C5["5️⃣ Swagger UI disponível<br/>/swagger-ui.html"]
        
        C1 --> C2 --> C3 --> C4 --> C5
    end

    style code_first fill:#E3F2FD,stroke:#1565C0
    style C1 fill:#BBDEFB
    style C2 fill:#BBDEFB
    style C3 fill:#BBDEFB
    style C4 fill:#90CAF9
    style C5 fill:#64B5F6,color:white
```

### Exemplo: Code First com Spring Boot

```java
// 1. Escreve o controller
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products")
public class ProductController {

    @Operation(summary = "Lista produtos")
    @GetMapping
    public List<ProductResponse> findAll() {
        return service.findAll();
    }

    @Operation(summary = "Cria produto")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        return service.create(request);
    }
}

// 2. SpringDoc gera o openapi.json AUTOMATICAMENTE
// 3. Swagger UI fica disponível em /swagger-ui.html
```

```
📦 Fluxo: Java Code → (SpringDoc) → OpenAPI Spec → Swagger UI
```

---

## 📝 Abordagem API First (Contract First)

```mermaid
flowchart TB
    subgraph api_first["📝 API FIRST — Contrato gera o código"]
        direction TB
        A1["1️⃣ Define o contrato OpenAPI<br/>openapi.yaml (YAML/JSON)"]
        A2["2️⃣ Revisa com stakeholders<br/>Frontend, Mobile, QA, PO"]
        A3["3️⃣ Todos concordam e aprovam<br/>Contrato vira 'lei'"]
        A4["4️⃣ Gera código automaticamente<br/>OpenAPI Generator / Swagger Codegen"]
        A5["5️⃣ Implementa a lógica de negócio<br/>nos métodos gerados"]
        
        A1 --> A2 --> A3 --> A4 --> A5
    end

    style api_first fill:#E8F5E9,stroke:#2E7D32
    style A1 fill:#C8E6C9
    style A2 fill:#C8E6C9
    style A3 fill:#A5D6A7
    style A4 fill:#81C784
    style A5 fill:#66BB6A,color:white
```

### Exemplo: API First com OpenAPI Generator

```yaml
# 1. Escreve o contrato openapi.yaml PRIMEIRO
openapi: 3.1.0
info:
  title: Products API
  version: 1.0.0
paths:
  /api/products:
    get:
      operationId: listProducts
      responses:
        '200':
          description: OK
          content:
            application/json:
              schema:
                type: array
                items:
                  $ref: '#/components/schemas/ProductResponse'
    post:
      operationId: createProduct
      requestBody:
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateProductRequest'
      responses:
        '201':
          description: Criado
```

```xml
<!-- 2. Configura o Maven Plugin para gerar código -->
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>7.2.0</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/openapi.yaml</inputSpec>
                <generatorName>spring</generatorName>
                <apiPackage>com.example.api</apiPackage>
                <modelPackage>com.example.model</modelPackage>
                <configOptions>
                    <interfaceOnly>true</interfaceOnly>
                    <useSpringBoot3>true</useSpringBoot3>
                    <useTags>true</useTags>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```

```java
// 3. Interface GERADA automaticamente pelo plugin
public interface ProductsApi {
    
    @GetMapping("/api/products")
    ResponseEntity<List<ProductResponse>> listProducts();
    
    @PostMapping("/api/products")
    ResponseEntity<ProductResponse> createProduct(@RequestBody CreateProductRequest request);
}

// 4. Você IMPLEMENTA a interface 
@RestController
public class ProductController implements ProductsApi {

    @Override
    public ResponseEntity<List<ProductResponse>> listProducts() {
        // Sua lógica de negócio aqui
        return ResponseEntity.ok(service.findAll());
    }

    @Override
    public ResponseEntity<ProductResponse> createProduct(CreateProductRequest request) {
        // Sua lógica de negócio aqui
        return ResponseEntity.status(201).body(service.create(request));
    }
}
```

```
📦 Fluxo: OpenAPI Spec → (Generator) → Interfaces Java → Implementação manual
```

---

## ⚔️ Comparação: API First vs Code First

```mermaid
flowchart LR
    subgraph cf["💻 CODE FIRST"]
        direction TB
        CF1["✅ Rápido para começar"]
        CF2["✅ Menos ferramentas"]
        CF3["✅ Ideal para prototipagem"]
        CF4["✅ Curva de aprendizado menor"]
        CF5["❌ Contrato é 'consequência'"]
        CF6["❌ Mudanças no código<br/>podem quebrar clientes"]
        CF7["❌ Documentação pode<br/>ficar desatualizada"]
    end

    subgraph af["📝 API FIRST"]
        direction TB
        AF1["✅ Contrato estável e claro"]
        AF2["✅ Times trabalham em paralelo"]
        AF3["✅ Validação automática"]
        AF4["✅ Gera clientes automaticamente"]
        AF5["❌ Setup inicial mais complexo"]
        AF6["❌ Mais ferramentas envolvidas"]
        AF7["❌ Mudanças na spec<br/>requerem nova geração"]
    end

    style cf fill:#E3F2FD,stroke:#1565C0
    style af fill:#E8F5E9,stroke:#2E7D32
    style CF1 fill:#C8E6C9,stroke:#43A047
    style CF2 fill:#C8E6C9,stroke:#43A047
    style CF3 fill:#C8E6C9,stroke:#43A047
    style CF4 fill:#C8E6C9,stroke:#43A047
    style CF5 fill:#FFCDD2,stroke:#E53935
    style CF6 fill:#FFCDD2,stroke:#E53935
    style CF7 fill:#FFCDD2,stroke:#E53935
    style AF1 fill:#C8E6C9,stroke:#43A047
    style AF2 fill:#C8E6C9,stroke:#43A047
    style AF3 fill:#C8E6C9,stroke:#43A047
    style AF4 fill:#C8E6C9,stroke:#43A047
    style AF5 fill:#FFCDD2,stroke:#E53935
    style AF6 fill:#FFCDD2,stroke:#E53935
    style AF7 fill:#FFCDD2,stroke:#E53935
```

---

## 🏢 API First — O Poder do Trabalho em Paralelo

```mermaid
sequenceDiagram
    participant PO as 👔 PO/Arquiteto
    participant BE as 💻 Backend
    participant FE as 🎨 Frontend
    participant QA as 🧪 QA
    participant MOB as 📱 Mobile

    rect rgb(200, 230, 201)
        Note over PO,MOB: 📝 Fase 1 — Definir o Contrato
        PO->>PO: Escreve openapi.yaml
        PO->>BE: Review técnico
        PO->>FE: Review de usabilidade
        BE-->>PO: Aprovado ✅
        FE-->>PO: Aprovado ✅
    end

    rect rgb(187, 222, 251)
        Note over PO,MOB: ⚡ Fase 2 — Todos Trabalham em PARALELO!
        PO->>BE: Gera interfaces Java
        PO->>FE: Gera cliente TypeScript
        PO->>QA: Gera testes automatizados
        PO->>MOB: Gera cliente Kotlin/Swift
        
        par Backend implementa
            BE->>BE: Implementa lógica
        and Frontend integra
            FE->>FE: Usa mock server
            FE->>FE: Integra com API tipada
        and QA testa
            QA->>QA: Testes com mock server
        and Mobile integra
            MOB->>MOB: Integra SDK gerado
        end
    end

    rect rgb(255, 249, 196)
        Note over PO,MOB: 🔗 Fase 3 — Integração
        BE-->>FE: API real disponível
        FE->>BE: Troca mock por API real
        QA->>BE: Roda testes contra API real
    end
```

> **Com API First, ninguém fica bloqueado esperando o backend terminar!**

---

## 🔧 Geração de Código — O que o Generator cria?

```mermaid
flowchart TB
    OAS["📝 openapi.yaml"]
    
    OAS -->|"spring"| JAVA["☕ Java/Spring Boot<br/>Interfaces + DTOs"]
    OAS -->|"typescript-axios"| TS["📘 TypeScript<br/>Cliente HTTP tipado"]
    OAS -->|"kotlin"| KT["🟣 Kotlin<br/>Cliente Android"]
    OAS -->|"swift5"| SW["🍎 Swift<br/>Cliente iOS"]
    OAS -->|"python"| PY["🐍 Python<br/>Cliente HTTP"]
    OAS -->|"html2"| DOC["📄 Documentação HTML"]

    style OAS fill:#1565C0,color:white,font-weight:bold
    style JAVA fill:#FFCC80,stroke:#E65100
    style TS fill:#BBDEFB,stroke:#1565C0
    style KT fill:#E1BEE7,stroke:#7B1FA2
    style SW fill:#FFCDD2,stroke:#C62828
    style PY fill:#C8E6C9,stroke:#2E7D32
    style DOC fill:#FFF9C4,stroke:#F9A825
```

---

## 📊 Quando usar cada abordagem?

```mermaid
flowchart TD
    Q{"🤔 Escolha a abordagem"}

    Q -->|"Projeto pequeno<br/>1-2 devs<br/>MVP/Protótipo<br/>API interna"| CF["💻 Code First<br/>Rápido e simples"]
    
    Q -->|"Múltiplos times<br/>API pública<br/>Muitos consumidores<br/>Microsserviços"| AF["📝 API First<br/>Contrato estável"]
    
    Q -->|"Projeto existente<br/>Quer melhorar docs"| HY["🔄 Híbrido<br/>Code First + Validação"]

    style Q fill:#FFF8E1,stroke:#F57F17,font-weight:bold
    style CF fill:#BBDEFB,stroke:#1565C0
    style AF fill:#C8E6C9,stroke:#2E7D32
    style HY fill:#E1BEE7,stroke:#7B1FA2
```

### Abordagem Híbrida (comum no mercado)

```
1. Começa com Code First (velocidade)
2. Exporta o OpenAPI gerado pelo SpringDoc
3. Adiciona validação CI/CD contra a spec
4. Trava mudanças breaking com ferramentas como oasdiff
```

---

## 🏢 Quem usa API First no mercado?

```mermaid
flowchart LR
    subgraph empresas["Empresas que usam API First"]
        direction TB
        E1["🏦 Bancos — Open Banking exige spec OpenAPI"]
        E2["☁️ Cloud Providers — AWS, Azure, GCP publicam specs"]
        E3["💳 Fintechs — Stripe, PayPal documentam com OAS"]
        E4["🛒 E-commerce — Mercado Livre, Magazine Luiza"]
        E5["📱 Plataformas — Twitter, Spotify, GitHub"]
    end

    style empresas fill:#E8F5E9,stroke:#2E7D32
    style E1 fill:#C8E6C9
    style E2 fill:#C8E6C9
    style E3 fill:#C8E6C9
    style E4 fill:#C8E6C9
    style E5 fill:#C8E6C9
```

---

## 📋 Resumo Rápido

| Aspecto | Code First | API First |
|---------|:----------:|:---------:|
| **Velocidade inicial** | 🚀 Rápido | 🐢 Mais setup |
| **Estabilidade do contrato** | ⚠️ Pode mudar | ✅ Estável |
| **Trabalho paralelo** | ❌ Backend bloqueia | ✅ Todos em paralelo |
| **Documentação** | Auto-gerada do código | Definida previamente |
| **Validação** | Em runtime | Em design time |
| **Geração de clientes** | Após implementar | Antes de implementar |
| **Ideal para** | MVPs, APIs internas | APIs públicas, multi-time |

---

## 💡 Dica do Instrutor

```
╔═══════════════════════════════════════════════════════════╗
║  Neste treinamento vamos usar CODE FIRST com SpringDoc.   ║
║  É a forma mais rápida de aprender e produzir APIs.       ║
║                                                           ║
║  Mas saiba que em grandes empresas, API First é a         ║
║  abordagem preferida quando há múltiplos consumidores.    ║
║                                                           ║
║  O importante é: TODA API deve ter documentação OpenAPI!  ║
╚═══════════════════════════════════════════════════════════╝
```
