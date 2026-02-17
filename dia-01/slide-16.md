# Slide 16: Swagger / OpenAPI — Documentando APIs

**Tópico complementar**

---

## 📜 O que é OpenAPI (Swagger)?

```
╔═══════════════════════════════════════════════════════════════╗
║  OpenAPI Specification (OAS) é o padrão da indústria para     ║
║  descrever, documentar e consumir APIs REST.                  ║
║                                                               ║
║  Swagger é o ECOSSISTEMA de ferramentas que trabalha com OAS. ║
╚═══════════════════════════════════════════════════════════════╝
```

---

## 🔄 Swagger vs OpenAPI — Qual a diferença?

```mermaid
timeline
    title Evolução Swagger → OpenAPI
    2011 : Swagger criado
         : Por Tony Tam (Wordnik)
         : Formato proprietário
    2015 : Swagger doado à Linux Foundation
         : Renomeado para OpenAPI Specification
         : Swagger vira nome das FERRAMENTAS
    2017 : OpenAPI 3.0
         : Novo formato, mais poderoso
         : Separação clara spec vs tools
    2021 : OpenAPI 3.1
         : Compatível com JSON Schema
         : Padrão da indústria
    2026 : OpenAPI 3.1.x
         : Adotado por 90%+ das APIs enterprise
```

```mermaid
flowchart LR
    subgraph spec["📝 ESPECIFICAÇÃO"]
        OAS["OpenAPI Specification<br/>(OAS 3.x)<br/>O PADRÃO"]
    end

    subgraph tools["🛠️ FERRAMENTAS (Swagger)"]
        UI["Swagger UI<br/>📺 Documentação visual"]
        ED["Swagger Editor<br/>✏️ Editor de specs"]
        CG["Swagger Codegen<br/>⚙️ Gera código"]
    end

    OAS --> UI
    OAS --> ED
    OAS --> CG

    style spec fill:#E3F2FD,stroke:#1565C0
    style tools fill:#FFF3E0,stroke:#E65100
    style OAS fill:#BBDEFB,stroke:#1565C0,font-weight:bold
    style UI fill:#FFCC80,stroke:#E65100
    style ED fill:#FFCC80,stroke:#E65100
    style CG fill:#FFCC80,stroke:#E65100
```

> **Resumo:** OpenAPI = a especificação (o contrato). Swagger = as ferramentas que usam essa especificação.

---

## 📄 Anatomia de um arquivo OpenAPI

```yaml
# openapi.yaml - Contrato da API de Produtos
openapi: 3.1.0
info:
  title: Products API
  description: API para gerenciamento de produtos
  version: 1.0.0
  contact:
    name: Equipe Backend
    email: backend@empresa.com

servers:
  - url: http://localhost:8080
    description: Ambiente local
  - url: https://api.empresa.com
    description: Produção

paths:
  /api/products:
    get:
      summary: Lista todos os produtos
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
                type: array
                items:
                  $ref: '#/components/schemas/ProductResponse'

    post:
      summary: Cria um novo produto
      operationId: createProduct
      tags:
        - Products
      requestBody:
        required: true
        content:
          application/json:
            schema:
              $ref: '#/components/schemas/CreateProductRequest'
      responses:
        '201':
          description: Produto criado com sucesso
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProductResponse'
        '400':
          description: Dados inválidos
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ErrorResponse'

  /api/products/{id}:
    get:
      summary: Busca produto por ID
      operationId: getProductById
      tags:
        - Products
      parameters:
        - name: id
          in: path
          required: true
          schema:
            type: integer
            format: int64
      responses:
        '200':
          description: Produto encontrado
          content:
            application/json:
              schema:
                $ref: '#/components/schemas/ProductResponse'
        '404':
          description: Produto não encontrado

components:
  schemas:
    CreateProductRequest:
      type: object
      required:
        - name
        - price
      properties:
        name:
          type: string
          minLength: 1
          maxLength: 100
          example: "Mouse Gamer"
        description:
          type: string
          example: "Mouse com 16000 DPI"
        price:
          type: number
          format: double
          minimum: 0.01
          example: 159.90
        category:
          type: string
          example: "Electronics"

    ProductResponse:
      type: object
      properties:
        id:
          type: integer
          format: int64
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

    ErrorResponse:
      type: object
      properties:
        error:
          type: string
        status:
          type: integer
        timestamp:
          type: string
          format: date-time
```

---

## 📺 Swagger UI — Documentação Interativa

```mermaid
flowchart TB
    subgraph swagger_ui["📺 Swagger UI — http://localhost:8080/swagger-ui.html"]
        direction TB
        H["🏷️ Products API v1.0.0"]
        
        subgraph endpoints["Endpoints"]
            GET1["🟢 GET /api/products — Lista todos"]
            POST1["🟡 POST /api/products — Cria novo"]
            GET2["🟢 GET /api/products/{id} — Busca por ID"]
            PUT1["🔵 PUT /api/products/{id} — Atualiza"]
            DEL1["🔴 DELETE /api/products/{id} — Remove"]
        end

        subgraph features["Funcionalidades"]
            F1["📋 Schemas dos DTOs"]
            F2["▶️ Try it out — Testar direto"]
            F3["📦 Exemplos de request/response"]
            F4["🔒 Autenticação configurável"]
        end
    end

    style swagger_ui fill:#E8F5E9,stroke:#2E7D32
    style H fill:#66BB6A,color:white,font-weight:bold
    style GET1 fill:#C8E6C9,stroke:#43A047
    style POST1 fill:#FFF9C4,stroke:#F9A825
    style GET2 fill:#C8E6C9,stroke:#43A047
    style PUT1 fill:#BBDEFB,stroke:#1565C0
    style DEL1 fill:#FFCDD2,stroke:#C62828
```

---

## 🚀 Swagger no Spring Boot — Configuração Rápida

### 1. Dependência (SpringDoc OpenAPI)

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springdoc</groupId>
    <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
    <version>2.3.0</version>
</dependency>
```

### 2. Configuração básica

```yaml
# application.yml
springdoc:
  api-docs:
    path: /api-docs           # JSON do OpenAPI
  swagger-ui:
    path: /swagger-ui.html    # Interface visual
    operations-sorter: method
    tags-sorter: alpha
```

### 3. Anotações no Controller

```java
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Gerenciamento de produtos")
public class ProductController {

    @Operation(
        summary = "Lista todos os produtos",
        description = "Retorna uma lista paginada de produtos"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso"),
        @ApiResponse(responseCode = "500", description = "Erro interno")
    })
    @GetMapping
    public List<ProductResponse> findAll(
        @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size
    ) {
        return service.findAll(page, size);
    }

    @Operation(summary = "Cria um novo produto")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Produto criado"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Dados do produto",
            required = true
        )
        @Valid @RequestBody CreateProductRequest request
    ) {
        return service.create(request);
    }
}
```

### 4. Anotações nos DTOs (Schemas)

```java
@Schema(description = "Request para criação de produto")
public record CreateProductRequest(
    @Schema(description = "Nome do produto", example = "Mouse Gamer", 
            minLength = 1, maxLength = 100)
    @NotBlank String name,
    
    @Schema(description = "Descrição do produto", example = "Mouse com 16000 DPI")
    String description,
    
    @Schema(description = "Preço do produto", example = "159.90", minimum = "0.01")
    @Positive BigDecimal price,
    
    @Schema(description = "Categoria", example = "Electronics")
    String category
) {}
```

---

## 🛠️ Ecossistema de Ferramentas OpenAPI

```mermaid
flowchart TB
    OAS["📝 OpenAPI Spec<br/>(YAML/JSON)"]
    
    OAS --> UI["📺 Swagger UI<br/>Documentação visual<br/>+ Try it out"]
    OAS --> CG["⚙️ OpenAPI Generator<br/>Gera código cliente/servidor<br/>Java, TypeScript, Python..."]
    OAS --> PM["📮 Postman<br/>Importa spec OpenAPI<br/>Gera collections"]
    OAS --> MOCK["🎭 Prism / WireMock<br/>Mock server a partir<br/>da spec"]
    OAS --> LINT["🔍 Spectral<br/>Linter para specs OpenAPI<br/>Valida boas práticas"]
    OAS --> TEST["🧪 Schemathesis<br/>Testes automáticos<br/>a partir da spec"]

    style OAS fill:#1565C0,color:white,font-weight:bold
    style UI fill:#C8E6C9,stroke:#2E7D32
    style CG fill:#FFCC80,stroke:#E65100
    style PM fill:#FFCC80,stroke:#E65100
    style MOCK fill:#E1BEE7,stroke:#7B1FA2
    style LINT fill:#BBDEFB,stroke:#1565C0
    style TEST fill:#BBDEFB,stroke:#1565C0
```

---

## 💡 Dica do Instrutor

```
╔═══════════════════════════════════════════════════════════╗
║  No dia 2, ao criar APIs com Spring Boot, usaremos         ║
║  SpringDoc para gerar o Swagger UI automaticamente.        ║
║                                                            ║
║  Acesse: http://localhost:8080/swagger-ui.html             ║
║  JSON:   http://localhost:8080/api-docs                    ║
╚═══════════════════════════════════════════════════════════╝
```
