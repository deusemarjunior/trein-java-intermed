# Slide 9: Design Patterns - Introdução

**Horário:** 11:00 - 12:30

---

## 🎨 O que são Design Patterns?

> **"Soluções reutilizáveis para problemas comuns no desenvolvimento de software"**
> 
> *— Gang of Four (GoF)*

```mermaid
mindmap
  root((Design Patterns))
    Criacionais
      Singleton
      Factory
      Builder
      Prototype
    Estruturais
      Adapter
      Decorator
      Facade
      Proxy
    Comportamentais
      Strategy
      Observer
      Template Method
      Command
```

---

## 📚 História

```mermaid
timeline
    title Evolução dos Design Patterns
    1977 : Christopher Alexander<br/>Padrões em Arquitetura
    1987 : Kent Beck e Ward Cunningham<br/>Padrões em Software
    1994 : Gang of Four (GoF)<br/>Livro "Design Patterns"
    1995-2000 : Martin Fowler<br/>Enterprise Patterns
    2000-hoje : Padrões modernos<br/>Microservices, Cloud, etc.
```

---

## 🎯 Foco do Dia

Vamos estudar 4 patterns essenciais para Spring Boot:

```mermaid
graph TD
    A[Design Patterns<br/>Essenciais] --> B[Strategy Pattern<br/>Comportamental]
    A --> C[Factory Pattern<br/>Criacional]
    A --> D[Builder Pattern<br/>Criacional]
    A --> E[Singleton Pattern<br/>Criacional]
    
    B -->|OCP| F[Múltiplas estratégias]
    C -->|SRP| G[Criação centralizada]
    D -->|Objetos complexos| H[Construção fluente]
    E -->|Já no Spring| I[@Component, @Service]
    
    style A fill:#4CAF50,stroke:#2E7D32,color:#fff
    style B fill:#2196F3,stroke:#1565C0,color:#fff
    style C fill:#2196F3,stroke:#1565C0,color:#fff
    style D fill:#2196F3,stroke:#1565C0,color:#fff
    style E fill:#2196F3,stroke:#1565C0,color:#fff
```

---

## ⚠️ Quando Usar Patterns?

```mermaid
flowchart TD
    A[Problema a resolver] --> B{Já viu problema<br/>similar antes?}
    B -->|Sim| C{Existe pattern<br/>conhecido?}
    B -->|Não| D[Resolver de forma simples]
    C -->|Sim| E{Pattern resolve<br/>bem o problema?}
    C -->|Não| D
    E -->|Sim| F[✅ Usar Pattern]
    E -->|Não| D
    
    D --> G{Problema se<br/>repete?}
    G -->|Sim| H[Considerar criar pattern]
    G -->|Não| I[✅ Manter solução simples]
    
    style F fill:#4CAF50,stroke:#2E7D32,color:#fff
    style I fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

## 💡 Princípios

```
✅ FAÇA:
- Use patterns para problemas conhecidos
- Entenda o problema antes de escolher o pattern
- Mantenha simplicidade
- Combine patterns quando necessário

❌ NÃO FAÇA:
- Force patterns onde não fazem sentido
- Use pattern só por usar (over-engineering)
- Ignore YAGNI (You Aren't Gonna Need It)
- Copie código sem entender
```

---

## 📊 Relação com SOLID

```mermaid
graph LR
    subgraph "SOLID Principles"
        S[SRP]
        O[OCP]
        L[LSP]
        I[ISP]
        D[DIP]
    end
    
    subgraph "Design Patterns"
        ST[Strategy]
        F[Factory]
        B[Builder]
        SG[Singleton]
    end
    
    O -->|implementa| ST
    S -->|promove| F
    S -->|promove| B
    D -->|usa| SG
    L -->|garante| ST
    
    style S fill:#4CAF50,stroke:#2E7D32,color:#fff
    style O fill:#4CAF50,stroke:#2E7D32,color:#fff
    style D fill:#4CAF50,stroke:#2E7D32,color:#fff
    style L fill:#4CAF50,stroke:#2E7D32,color:#fff
```
