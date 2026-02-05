# Slide 18: Clean Architecture e Recap

---

## 🎯 Clean Architecture (Uncle Bob)

```mermaid
graph TB
    subgraph "External"
        UI[UI<br/>Web, Mobile]
        DB[(Database)]
        EXT[External<br/>APIs]
        DEV[Devices]
    end
    
    subgraph "Frameworks & Drivers"
        WEB[Web Framework]
        ORM[ORM]
        HTTP[HTTP Client]
    end
    
    subgraph "Interface Adapters"
        CTRL[Controllers]
        PRES[Presenters]
        GATE[Gateways]
    end
    
    subgraph "Application Business Rules"
        UC[Use Cases]
    end
    
    subgraph "Enterprise Business Rules"
        ENT[Entities]
    end
    
    UI --> WEB
    DB --> ORM
    EXT --> HTTP
    DEV --> WEB
    
    WEB --> CTRL
    ORM --> GATE
    HTTP --> GATE
    
    CTRL --> UC
    PRES --> UC
    GATE --> UC
    
    UC --> ENT
    
    style ENT fill:#4CAF50,stroke:#2E7D32,color:#fff,stroke-width:4px
    style UC fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

## 🎯 Regra da Dependência

```mermaid
graph LR
    A[Frameworks<br/>& Drivers] -->|depende| B[Interface<br/>Adapters]
    B -->|depende| C[Application<br/>Business Rules]
    C -->|depende| D[Enterprise<br/>Business Rules]
    
    Note1[✅ Dependências apontam<br/>sempre para DENTRO]
    Note2[✅ Core não conhece<br/>detalhes externos]
    
    style D fill:#4CAF50,stroke:#2E7D32,color:#fff,stroke-width:4px
```

**Regra de Ouro:** 
> **Código nas camadas internas não deve conhecer nada das camadas externas**

---

## 📊 Comparação de Arquiteturas

```mermaid
quadrantChart
    title Arquiteturas: Complexidade vs Benefícios
    x-axis Baixa Complexidade --> Alta Complexidade
    y-axis Poucos Benefícios --> Muitos Benefícios
    
    Layered: [0.2, 0.4]
    Hexagonal: [0.6, 0.8]
    Clean: [0.8, 0.9]
    Big Ball of Mud: [0.1, 0.1]
```

---

## 🏗️ Evolução Arquitetural

```mermaid
timeline
    title Evolução das Arquiteturas
    
    section Tradicional
        Monolito : Tudo em um lugar
        3-Tier : Apresentação, Negócio, Dados
    
    section Moderna
        Layered : Camadas bem definidas
        Hexagonal : Ports & Adapters
    
    section Atual
        Clean : Regra da dependência
        Microservices : Serviços independentes
```

---

## 📚 Recap do Dia 3

### 🎯 SOLID Principles

```mermaid
mindmap
  root((SOLID))
    S
      Single Responsibility
      Uma razão para mudar
    O
      Open/Closed
      Strategy Pattern
    L
      Liskov Substitution
      Contratos preservados
    I
      Interface Segregation
      Interfaces pequenas
    D
      Dependency Inversion
      Abstrações sobre detalhes
```

---

### 🎨 Design Patterns

```mermaid
graph LR
    subgraph "Comportamental"
        ST[Strategy<br/>Múltiplas estratégias]
    end
    
    subgraph "Criacional"
        F[Factory<br/>Criação centralizada]
        B[Builder<br/>Objetos complexos]
        S[Singleton<br/>Instância única]
    end
    
    ST -->|OCP| USE1[Desconto<br/>Pagamento]
    F -->|SRP| USE2[Notificação<br/>Payment]
    B -->|Legibilidade| USE3[User<br/>Order]
    S -->|Spring| USE4[@Component<br/>@Service]
    
    style ST fill:#9C27B0,stroke:#7B1FA2,color:#fff
    style F fill:#4CAF50,stroke:#2E7D32,color:#fff
    style B fill:#4CAF50,stroke:#2E7D32,color:#fff
    style S fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

### 📐 DDD Building Blocks

```mermaid
graph TB
    subgraph "Domain Layer"
        E[Entities<br/>ID único, mutável]
        VO[Value Objects<br/>Imutável, sem ID]
        A[Aggregates<br/>Consistência]
        DS[Domain Services<br/>Lógica entre entidades]
    end
    
    subgraph "Persistência"
        R[Repositories<br/>Interface no domínio]
    end
    
    A --> E
    E --> VO
    DS --> E
    R -.persiste.-> A
    
    EX1[Order, Customer] -.exemplo.-> E
    EX2[Money, Email] -.exemplo.-> VO
    EX3[Order + Items] -.exemplo.-> A
    
    style E fill:#4CAF50,stroke:#2E7D32,color:#fff
    style VO fill:#2196F3,stroke:#1565C0,color:#fff
    style A fill:#FF9800,stroke:#F57C00,color:#fff
```

---

### 🏛️ Arquiteturas

```mermaid
graph LR
    subgraph "Layered"
        L1[Presentation] --> L2[Business] --> L3[Data]
        L3 --> L4[(DB)]
    end
    
    subgraph "Hexagonal"
        H1[REST] --> H2[Use Cases]
        H3[CLI] --> H2
        H2 --> H4[Domain]
        H2 --> H5[Ports]
        H6[DB] -.impl.-> H5
        H7[Email] -.impl.-> H5
    end
    
    style L1 fill:#f44336,stroke:#c62828,color:#fff
    style L2 fill:#f44336,stroke:#c62828,color:#fff
    style H4 fill:#4CAF50,stroke:#2E7D32,color:#fff
    style H2 fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

## 🎯 Principais Aprendizados

```mermaid
mindmap
  root((Dia 3))
    Código Limpo
      SOLID principles
      Design patterns
      Refatoração
    Modelagem
      Entities vs Value Objects
      Aggregates
      Ubiquitous Language
    Arquitetura
      Separação de concerns
      Inversão de dependência
      Testabilidade
    Boas Práticas
      DRY
      YAGNI
      KISS
```

---

## 📊 Antes e Depois

### ❌ Código sem princípios

```java
@Service
public class OrderService {
    public void processOrder(Order order) {
        // Validação
        // Cálculo de desconto (if/else gigante)
        // Salva no MySQL diretamente
        // Envia email
        // Envia SMS
        // Atualiza cache
        // Gera PDF
        // Difícil testar
        // Impossível reutilizar
        // Acoplamento alto
    }
}
```

### ✅ Código com princípios

```java
@Service
@RequiredArgsConstructor
public class OrderApplicationService {  // SRP
    private final OrderValidator validator;  // SRP
    private final DiscountStrategy discountStrategy;  // OCP, Strategy
    private final OrderRepository repository;  // DIP, Repository
    private final NotificationFactory notificationFactory;  // Factory
    
    @Transactional
    public OrderResponse processOrder(CreateOrderCommand command) {
        // Cada responsabilidade isolada
        // Fácil testar
        // Fácil reutilizar
        // Baixo acoplamento
    }
}
```

---

## 🎯 Regras de Ouro

```mermaid
graph TD
    A[Código Limpo] --> B[SOLID sempre]
    A --> C[Patterns quando necessário]
    A --> D[DDD para domínio complexo]
    A --> E[Arquitetura adequada]
    
    B --> B1[Não force<br/>Use com bom senso]
    C --> C1[YAGNI<br/>Não over-engineer]
    D --> D1[Value Objects<br/>Entities<br/>Aggregates]
    E --> E1[Layered para simples<br/>Hexagonal para complexo]
    
    style A fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

## 🔍 Sinais de Código Ruim vs Bom

| Aspecto | ❌ Código Ruim | ✅ Código Bom |
|---------|---------------|--------------|
| Classes | >500 linhas | <200 linhas |
| Métodos | >50 linhas | <20 linhas |
| Parâmetros | >5 parâmetros | <4 parâmetros |
| Dependências | >10 dependências | <5 dependências |
| Testes | Difícil testar | Fácil testar |
| Nomes | Genéricos (Manager, Util) | Específicos |
| Acoplamento | Alto (new, static) | Baixo (DI) |
| Duplicação | Código repetido | DRY |

---

## 📚 Recursos para Continuar

```mermaid
mindmap
  root((Recursos))
    Livros
      Clean Code - Uncle Bob
      Clean Architecture - Uncle Bob
      Domain-Driven Design - Eric Evans
      Refactoring - Martin Fowler
    Online
      Refactoring Guru
      Martin Fowler Blog
      DDD Community
    Prática
      Code Katas
      Refactoring exercises
      Pet Projects
```

---

## 🎯 Próximos Passos

1. **Refatorar código existente**
   - Identificar violações de SOLID
   - Aplicar patterns onde faz sentido
   - Melhorar testabilidade

2. **Estudar mais patterns**
   - Adapter, Decorator, Observer
   - Template Method, Command
   - Proxy, Facade

3. **Aprofundar em DDD**
   - Bounded Contexts
   - Context Mapping
   - Event Storming

4. **Praticar arquitetura**
   - Implementar Hexagonal
   - Comparar com Layered
   - Avaliar trade-offs

---

## 💡 Mensagem Final

```
⚠️ Lembre-se:

✅ SOLID são princípios, não leis
✅ Patterns são ferramentas, não objetivos
✅ Simplicidade > Complexidade
✅ Código que funciona > Código perfeito
✅ Melhoria contínua > Perfeição

🎯 Regra de Ouro:
"Make it work, make it right, make it fast"
- Kent Beck

Primeiro faça funcionar
Depois faça certo (refatore)
Por último otimize (se necessário)
```

---

## 🙏 Obrigado!

**Dúvidas?**

📧 Email: [seu-email]  
💼 LinkedIn: [seu-linkedin]  
🐙 GitHub: [seu-github]

---

## 📝 Feedback

```
Por favor, responda:

1. O que você mais gostou no dia de hoje?
2. O que poderia melhorar?
3. Qual tópico você gostaria de aprofundar mais?
4. Como você pretende aplicar o aprendizado?

Obrigado pela participação! 🎉
```
