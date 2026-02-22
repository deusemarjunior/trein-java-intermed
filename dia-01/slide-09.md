# Slide 9: Fundamentos Web & Servlets

**Horário:** 13:00 - 13:30

---

## 🖥️ Tipos de Aplicações Java — Comparativo

Antes de falar de Web, é importante entender **onde as aplicações Web se encaixam** no ecossistema Java:

```mermaid
block-beta
    columns 3
    block:desktop:1
        columns 1
        dt["🖥️ DESKTOP (GUI)"]
        d1["Swing / JavaFX"]
        d2["Roda no PC do usuário<br/>JVM local"]
        d3["Ex: IntelliJ, Eclipse,<br/>apps bancários antigos"]
        d4["❌ Difícil distribuir<br/>❌ Cada máquina precisa da JVM"]
    end
    block:console:1
        columns 1
        ct["⌨️ CONSOLE"]
        c1["main() simples"]
        c2["Roda no terminal<br/>sem interface gráfica"]
        c3["Ex: scripts de batch,<br/>ETL, automações"]
        c4["❌ Sem interface p/ usuário<br/>❌ Normalmente uso interno"]
    end
    block:web:1
        columns 1
        wt["🌐 WEB / SERVER-SIDE"]
        w1["Servlet + JSP / Spring Boot"]
        w2["Roda dentro de um<br/>SERVIDOR DE APLICAÇÃO"]
        w3["Ex: e-commerce, APIs REST,<br/>banking, portais"]
        w4["✅ Acesso via browser/API<br/>✅ Deploy uma vez, todos usam<br/>✅ Escala horizontalmente"]
    end

    style desktop fill:#FFCDD2,stroke:#E53935
    style console fill:#FFF9C4,stroke:#F9A825
    style web fill:#C8E6C9,stroke:#2E7D32
    style dt fill:#E53935,color:#fff,font-weight:bold
    style ct fill:#F9A825,color:#fff,font-weight:bold
    style wt fill:#2E7D32,color:#fff,font-weight:bold
    style d1 fill:#FFEBEE
    style d2 fill:#FFEBEE
    style d3 fill:#FFEBEE
    style d4 fill:#FFCDD2
    style c1 fill:#FFFDE7
    style c2 fill:#FFFDE7
    style c3 fill:#FFFDE7
    style c4 fill:#FFF9C4
    style w1 fill:#E8F5E9
    style w2 fill:#E8F5E9
    style w3 fill:#E8F5E9
    style w4 fill:#C8E6C9
```

> **Hoje, 90%+ dos projetos Java no mercado são aplicações Web / APIs.**

---

## 📡 Tipos de Aplicações Web — REST, SOAP e GraphQL

Dentro do mundo Web, existem **diferentes estilos de comunicação** entre cliente e servidor:

### 🟢 REST API — O padrão do mercado

```mermaid
flowchart LR
    REST["🟢 REST API"] --> r1["✅ Arquitetura mais<br/>popular hoje"]
    REST --> r2["✅ Verbos HTTP<br/>GET POST PUT DELETE"]
    REST --> r3["✅ Formato JSON<br/>leve e legível"]
    REST --> r4["✅ Stateless<br/>sem estado no servidor"]
    REST --> r5["✅ Endpoints por recurso<br/>/api/products<br/>/api/orders"]

    style REST fill:#2E7D32,color:#fff,stroke:#1B5E20,font-size:16px
    style r1 fill:#C8E6C9,stroke:#2E7D32
    style r2 fill:#C8E6C9,stroke:#2E7D32
    style r3 fill:#C8E6C9,stroke:#2E7D32
    style r4 fill:#C8E6C9,stroke:#2E7D32
    style r5 fill:#C8E6C9,stroke:#2E7D32
```

### 🔵 SOAP — O protocolo corporativo

```mermaid
flowchart LR
    SOAP["🔵 SOAP"] --> s1["📋 Protocolo mais<br/>antigo e formal"]
    SOAP --> s2["📋 Usa apenas POST<br/>com envelope XML"]
    SOAP --> s3["📋 Formato XML<br/>obrigatório"]
    SOAP --> s4["📋 Contrato rígido<br/>via WSDL"]
    SOAP --> s5["📋 Muito usado em bancos,<br/>governo e legados"]

    style SOAP fill:#1565C0,color:#fff,stroke:#0D47A1,font-size:16px
    style s1 fill:#BBDEFB,stroke:#1565C0
    style s2 fill:#BBDEFB,stroke:#1565C0
    style s3 fill:#BBDEFB,stroke:#1565C0
    style s4 fill:#BBDEFB,stroke:#1565C0
    style s5 fill:#BBDEFB,stroke:#1565C0
```

### 🟣 GraphQL — O flexível

```mermaid
flowchart LR
    GQL["🟣 GraphQL"] --> g1["🚀 Linguagem de consulta<br/>criada pelo Facebook"]
    GQL --> g2["🚀 Um único endpoint<br/>POST /graphql"]
    GQL --> g3["🚀 Cliente escolhe<br/>exatamente os campos"]
    GQL --> g4["🚀 Evita over-fetching<br/>e under-fetching"]
    GQL --> g5["🚀 Usado em apps mobile<br/>e front-ends modernos"]

    style GQL fill:#7B1FA2,color:#fff,stroke:#4A148C,font-size:16px
    style g1 fill:#E1BEE7,stroke:#7B1FA2
    style g2 fill:#E1BEE7,stroke:#7B1FA2
    style g3 fill:#E1BEE7,stroke:#7B1FA2
    style g4 fill:#E1BEE7,stroke:#7B1FA2
    style g5 fill:#E1BEE7,stroke:#7B1FA2
```

### Comparação na prática — Buscar produto por ID

```mermaid
sequenceDiagram
    participant C as 🌐 Cliente
    participant S as 🖥️ Servidor

    rect rgb(200, 230, 201)
        Note over C,S: 🟢 REST API
        C->>S: GET /api/products/42<br/>Accept: application/json
        S-->>C: 200 OK<br/>{"id":42, "name":"Mouse", "price":59.90}
    end

    rect rgb(187, 222, 251)
        Note over C,S: 🔵 SOAP
        C->>S: POST /ws/products<br/>Content-Type: text/xml
        S-->>C: 200 OK — XML Envelope com Product id=42, name=Mouse
    end

    rect rgb(225, 190, 231)
        Note over C,S: 🟣 GraphQL
        C->>S: POST /graphql<br/>{ product(id:42) { name, price } }
        S-->>C: 200 OK<br/>{"data":{"product":{"name":"Mouse","price":59.90}}}
    end
```

### Quando usar cada um?

```mermaid
flowchart LR
    Q{"Qual tipo<br/>de API?"}

    Q -->|"API pública<br/>CRUD simples<br/>Microsserviços"| R["🟢 REST<br/>✅ Escolha padrão"]
    Q -->|"Sistema legado<br/>Banco/governo<br/>Contrato rígido"| S["🔵 SOAP<br/>⚠️ Quando exigido"]
    Q -->|"Front-end complexo<br/>Mobile apps<br/>Muitas entidades"| G["🟣 GraphQL<br/>🚀 Flexibilidade"]

    style Q fill:#FFF8E1,stroke:#F57F17
    style R fill:#C8E6C9,stroke:#2E7D32
    style S fill:#BBDEFB,stroke:#1565C0
    style G fill:#E1BEE7,stroke:#7B1FA2
```

> **Neste treinamento vamos focar em REST API** — o padrão dominante no mercado Java com Spring Boot.

---

## ☕ Java EE (Jakarta EE) — O Padrão Corporativo

Java EE (renomeado para **Jakarta EE** desde 2019) é a **especificação oficial** para construir aplicações corporativas em Java.

```mermaid
mindmap
    root((☕ Jakarta EE<br/>Java EE))
        📋 Servlet API
            Processar HTTP
        📋 JPA
            Persistência ORM
        📋 JAX-RS
            APIs REST declarativas
        📋 CDI
            Injeção de dependência
        📋 Bean Validation
            Validação de dados
        📋 JMS
            Mensageria
        📋 JSON-B / JSON-P
            Processamento JSON
```

> ⚠️ **Java EE define O QUE fazer, não COMO.** Quem implementa são os **Servidores de Aplicação**.

### Servidores de Aplicação

```mermaid
flowchart LR
    subgraph leve["🪶 Servlet Container - Leve"]
        T["🐱 Apache Tomcat<br/><i>Maioria dos projetos Spring</i>"]
        J["⚡ Jetty<br/><i>Aplicações embarcadas</i>"]
    end
    subgraph full["🏢 Full Java EE"]
        W["🐘 WildFly<br/>(antigo JBoss)<br/><i>Stack Red Hat</i>"]
        G["🐟 GlassFish<br/><i>Referência oficial</i>"]
        WS["🔵 WebSphere (IBM)<br/><i>Bancos e seguradoras</i>"]
    end

    style leve fill:#E8F5E9,stroke:#2E7D32
    style full fill:#E3F2FD,stroke:#1565C0
    style T fill:#C8E6C9,stroke:#2E7D32,color:#1B5E20
    style J fill:#C8E6C9,stroke:#2E7D32,color:#1B5E20
    style W fill:#BBDEFB,stroke:#1565C0,color:#0D47A1
    style G fill:#BBDEFB,stroke:#1565C0,color:#0D47A1
    style WS fill:#BBDEFB,stroke:#1565C0,color:#0D47A1
```

---

## 🐱 Apache Tomcat — O Mais Popular

O **Tomcat** é o servidor mais utilizado no ecossistema Java. Ele implementa a **Servlet API** e é a base de execução do Spring Boot.

```mermaid
flowchart TB
    subgraph Tomcat["🐱 Apache Tomcat"]
        direction TB
        C[Connector<br/>Recebe HTTP na porta 8080]
        E[Engine<br/>Processa requisições]
        W[Web Application<br/>.war ou embedded]
        S1[Servlet 1<br/>/api/products]
        S2[Servlet 2<br/>/api/orders]
        S3[Filter<br/>CORS, Auth]
    end

    Browser["🌐 Browser / Client"] -->|HTTP Request| C
    C --> E
    E --> S3
    S3 --> W
    W --> S1
    W --> S2

    style Tomcat fill:#FFF3E0
    style Browser fill:#E3F2FD
    style C fill:#FFCC80
    style S1 fill:#A5D6A7
    style S2 fill:#A5D6A7
    style S3 fill:#EF9A9A
```

### Duas formas de usar o Tomcat

```mermaid
flowchart LR
    subgraph standalone["1️⃣ TOMCAT STANDALONE - Tradicional"]
        direction TB
        SA["Instalar Tomcat<br/>no servidor"] --> SB["Empacotar app<br/>como .war"]
        SB --> SC["Deploy do .war<br/>no Tomcat"]
        SC --> SD["❌ Mais complexo<br/>❌ Projetos legados"]
    end

    subgraph embedded["2️⃣ TOMCAT EMBEDDED - Moderno ✅"]
        direction TB
        EA["Tomcat como<br/>dependência Maven"] --> EB["Roda direto<br/>pelo main()"]
        EB --> EC["Um único .jar<br/>executável"]
        EC --> ED["✅ É assim que o<br/>Spring Boot funciona!"]
    end

    style standalone fill:#FFF3E0,stroke:#E65100
    style embedded fill:#E8F5E9,stroke:#2E7D32
    style SD fill:#FFCDD2,stroke:#C62828
    style ED fill:#C8E6C9,stroke:#2E7D32
```

```java
// Exemplo: Tomcat Embedded em 10 linhas
public class App {
    public static void main(String[] args) throws Exception {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        // Registrar servlet
        Context ctx = tomcat.addContext("", null);
        Tomcat.addServlet(ctx, "product", new ProductServlet());
        ctx.addServletMappingDecoded("/api/products/*", "product");

        tomcat.start();
        tomcat.getServer().await(); // Fica ouvindo requisições
    }
}
```

---

## 🔄 Evolução: Servlet → Spring Boot

```mermaid
timeline
    title Evolução do Java Web
    1997 : Servlet 1.0
         : Java web nasce
    2001 : JSP + Servlet
         : Páginas dinâmicas no servidor
    2004 : Spring Framework
         : Simplifica Java EE
    2006 : JAX-RS (REST)
         : APIs REST padronizadas
    2014 : Spring Boot
         : Servlet + Tomcat embedded, sem XML
    2026 : Spring Boot 3.x
         : Jakarta EE 10, Java 21+, GraalVM
```

```mermaid
flowchart LR
    A["Servlet"] -->|+| B["Tomcat<br/>Embedded"]
    B -->|+| C["Auto-config"]
    C ==>|=| D["🚀 Spring Boot"]

    style A fill:#FFCC80,stroke:#E65100
    style B fill:#FFCC80,stroke:#E65100
    style C fill:#FFCC80,stroke:#E65100
    style D fill:#66BB6A,stroke:#2E7D32,color:#fff
```

> **Tudo que o Spring faz, passa por Servlets por baixo!**

---

## 🌐 Como funciona uma aplicação Web?

### Cliente-Servidor

```mermaid
sequenceDiagram
    participant C as Cliente<br/>(Browser/App)
    participant S as Servidor<br/>(Tomcat + Servlet)
    participant D as Database
    
    C->>S: 1. HTTP Request<br/>GET /api/products
    Note over S: Servlet recebe
    S->>S: 2. Processa<br/>Servlet → DAO
    S->>D: 3. Consulta (JDBC)
    D-->>S: 4. Retorna dados
    S-->>C: 5. HTTP Response<br/>200 OK + JSON
    Note over C: Exibe dados
```

---

## 🔧 O que é um Servlet?

Classe Java que processa requisições HTTP no servidor. É a base de **toda** aplicação web Java.

### Ciclo de vida

```mermaid
stateDiagram-v2
    [*] --> init: Container cria
    init --> service: Request chega
    service --> doGet: GET
    service --> doPost: POST
    service --> doPut: PUT
    service --> doDelete: DELETE
    doGet --> service
    doPost --> service
    doPut --> service
    doDelete --> service
    service --> destroy: Container encerra
    destroy --> [*]
```

---

## 📡 REST com Servlets

### Mapeamento HTTP → Servlet

```mermaid
flowchart LR
    subgraph req["📨 HTTP Request"]
        G1["GET /api/products"]
        G2["GET /api/products/123"]
        P["POST /api/products"]
        U["PUT /api/products/123"]
        DEL["DELETE /api/products/123"]
    end

    subgraph servlet["☕ ProductServlet"]
        DG["doGet()"]
        DP["doPost()"]
        DU["doPut()"]
        DD["doDelete()"]
    end

    subgraph acao["⚡ Ação"]
        A1["Lista produtos"]
        A2["Busca produto"]
        A3["Cria produto"]
        A4["Atualiza produto"]
        A5["Deleta produto"]
    end

    G1 --> DG --> A1
    G2 --> DG --> A2
    P --> DP --> A3
    U --> DU --> A4
    DEL --> DD --> A5

    style req fill:#E3F2FD,stroke:#1565C0
    style servlet fill:#FFF3E0,stroke:#E65100
    style acao fill:#E8F5E9,stroke:#2E7D32
```

### Servlet Básico

```java
@WebServlet("/api/products/*")
public class ProductServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        // 1. Ler path para detectar se é lista ou busca por ID
        String pathInfo = request.getPathInfo(); // null ou "/123"

        // 2. Configurar JSON na resposta
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // 3. Processar e retornar
        if (pathInfo == null || pathInfo.equals("/")) {
            // Listar todos
            List<Product> products = productDAO.findAll();
            String json = gson.toJson(products);
            response.getWriter().write(json);
        } else {
            // Buscar por ID
            Long id = Long.parseLong(pathInfo.substring(1));
            Product product = productDAO.findById(id);
            if (product != null) {
                response.getWriter().write(gson.toJson(product));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("{\"error\":\"Product not found\"}");
            }
        }
    }
}
```

---

## 🔌 JDBC - Java Database Connectivity

### O que é?

API padrão Java para conectar com bancos de dados relacionais.

```mermaid
flowchart LR
    A[Aplicação Java] --> B[JDBC API]
    B --> C[JDBC Driver]
    C --> D[(Database)]
    
    style A fill:#87CEEB
    style B fill:#90EE90
    style C fill:#FFB6C1
    style D fill:#DDA0DD
```

### Componentes principais

| Componente | Descrição |
|-----------|-----------|
| `DriverManager` | Gerencia drivers de banco |
| `Connection` | Conexão com o banco |
| `PreparedStatement` | SQL parametrizado (seguro!) |
| `ResultSet` | Resultado de uma query |
| `DataSource` | Pool de conexões |

---

## ⚠️ JDBC vs ORM (JPA/Hibernate)

```mermaid
flowchart LR
    subgraph jdbc["🔧 JDBC — Hoje"]
        direction TB
        J1["✅ Controle total do SQL"]
        J2["✅ Leve, sem overhead"]
        J3["✅ Entendimento profundo"]
        J4["❌ Mais boilerplate"]
        J5["❌ Mapeamento manual<br/>objeto ↔ tabela"]
        J1 ~~~ J2 ~~~ J3 ~~~ J4 ~~~ J5
    end

    subgraph vs[" "]
        direction TB
        V["⚔️<br/>VS"]
    end

    subgraph jpa["🪄 JPA/Hibernate — Dia 2"]
        direction TB
        H1["✅ Mapeamento automático"]
        H2["✅ Menos código"]
        H3["✅ Queries derivadas"]
        H4["❌ Mágica pode esconder<br/>problemas"]
        H5["❌ Curva de aprendizado<br/>maior"]
        H1 ~~~ H2 ~~~ H3 ~~~ H4 ~~~ H5
    end

    jdbc --- vs --- jpa

    style jdbc fill:#E3F2FD,stroke:#1565C0,min-width:300px
    style jpa fill:#F3E5F5,stroke:#7B1FA2,min-width:300px
    style vs fill:none,stroke:none
    style V fill:#FFF8E1,stroke:#F57F17,font-size:18px,font-weight:bold
    style J1 fill:#C8E6C9,stroke:#43A047
    style J2 fill:#C8E6C9,stroke:#43A047
    style J3 fill:#C8E6C9,stroke:#43A047
    style J4 fill:#FFCDD2,stroke:#E53935
    style J5 fill:#FFCDD2,stroke:#E53935
    style H1 fill:#C8E6C9,stroke:#43A047
    style H2 fill:#C8E6C9,stroke:#43A047
    style H3 fill:#C8E6C9,stroke:#43A047
    style H4 fill:#FFCDD2,stroke:#E53935
    style H5 fill:#FFCDD2,stroke:#E53935
```

---

## 💡 Por que aprender Servlet + JDBC primeiro?

```mermaid
flowchart TB
    subgraph motivos["💡 Por que aprender a base?"]
        direction TB
        M1["1️⃣ Spring Boot USA Servlets<br/>por baixo dos panos"]
        M2["2️⃣ JDBC é a BASE<br/>do JPA/Hibernate"]
        M3["3️⃣ Entender a base<br/>= debugar melhor"]
        M4["4️⃣ Muitos sistemas legados<br/>usam Servlet + JDBC"]
    end

    M1 -.- D1["@RestController é um<br/>Servlet embelezado"]
    M2 -.- D2["Todo ORM gera SQL<br/>que roda via JDBC"]
    M3 -.- D3["Quando Spring falha,<br/>você sabe investigar"]
    M4 -.- D4["Mercado real tem<br/>muito código assim"]

    style motivos fill:#FFF8E1,stroke:#F57F17
    style M1 fill:#FFECB3,stroke:#FF8F00
    style M2 fill:#FFECB3,stroke:#FF8F00
    style M3 fill:#FFECB3,stroke:#FF8F00
    style M4 fill:#FFECB3,stroke:#FF8F00
    style D1 fill:#E8F5E9,stroke:#66BB6A
    style D2 fill:#E8F5E9,stroke:#66BB6A
    style D3 fill:#E8F5E9,stroke:#66BB6A
    style D4 fill:#E8F5E9,stroke:#66BB6A
```

