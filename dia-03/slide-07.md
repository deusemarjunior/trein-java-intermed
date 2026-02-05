# Slide 7: Interface Segregation Principle (ISP)

---

## 📖 Definição

> **"Nenhum cliente deve ser forçado a depender de métodos que não utiliza"**
> 
> *— Robert C. Martin*

```mermaid
graph LR
    A[Interface Grande] -->|força| B[Cliente A<br/>usa 20%]
    A -->|força| C[Cliente B<br/>usa 30%]
    A -->|força| D[Cliente C<br/>usa 10%]
    
    E[Interface Específica 1] -->|usa| F[Cliente A<br/>usa 100%]
    G[Interface Específica 2] -->|usa| H[Cliente B<br/>usa 100%]
    I[Interface Específica 3] -->|usa| J[Cliente C<br/>usa 100%]
    
    style A fill:#f44336,stroke:#c62828,color:#fff
    style E fill:#4CAF50,stroke:#2E7D32,color:#fff
    style G fill:#4CAF50,stroke:#2E7D32,color:#fff
    style I fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

## ❌ Fat Interface (Interface Gorda)

```java
// ❌ Interface muito grande - força implementações desnecessárias
public interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
    void writeCode();
    void reviewCode();
    void deployToProduction();
    void manageTeam();
    void createBudget();
}

// Implementação forçada a ter métodos que não fazem sentido
public class Developer implements Worker {
    public void work() { /* implementa */ }
    public void eat() { /* implementa */ }
    public void sleep() { /* implementa */ }
    public void attendMeeting() { /* implementa */ }
    public void writeCode() { /* implementa */ }
    public void reviewCode() { /* implementa */ }
    public void deployToProduction() { /* implementa */ }
    
    // ⚠️ Developer não gerencia equipe!
    public void manageTeam() {
        throw new UnsupportedOperationException();
    }
    
    // ⚠️ Developer não cria orçamento!
    public void createBudget() {
        throw new UnsupportedOperationException();
    }
}
```

---

## 📊 Problema Visualizado

```mermaid
classDiagram
    class Worker {
        <<interface>>
        +work()
        +eat()
        +sleep()
        +attendMeeting()
        +writeCode()
        +reviewCode()
        +deployToProduction()
        +manageTeam()
        +createBudget()
    }
    
    class Developer {
        +work() ✅
        +eat() ✅
        +sleep() ✅
        +attendMeeting() ✅
        +writeCode() ✅
        +reviewCode() ✅
        +deployToProduction() ✅
        +manageTeam() ❌ throws Exception
        +createBudget() ❌ throws Exception
    }
    
    Worker <|.. Developer
    
    style Worker fill:#f44336,stroke:#c62828,color:#fff
    style Developer fill:#FF9800,stroke:#F57C00,color:#fff
```

---

## ✅ Aplicando ISP - Interfaces Segregadas

```mermaid
classDiagram
    class Workable {
        <<interface>>
        +work()
    }
    
    class Eatable {
        <<interface>>
        +eat()
    }
    
    class Sleepable {
        <<interface>>
        +sleep()
    }
    
    class Codeable {
        <<interface>>
        +writeCode()
        +reviewCode()
    }
    
    class Deployable {
        <<interface>>
        +deployToProduction()
    }
    
    class Manageable {
        <<interface>>
        +manageTeam()
        +createBudget()
    }
    
    class Developer {
        +work()
        +eat()
        +sleep()
        +writeCode()
        +reviewCode()
        +deployToProduction()
    }
    
    class Manager {
        +work()
        +eat()
        +sleep()
        +manageTeam()
        +createBudget()
    }
    
    Workable <|.. Developer
    Eatable <|.. Developer
    Sleepable <|.. Developer
    Codeable <|.. Developer
    Deployable <|.. Developer
    
    Workable <|.. Manager
    Eatable <|.. Manager
    Sleepable <|.. Manager
    Manageable <|.. Manager
    
    style Developer fill:#4CAF50,stroke:#2E7D32,color:#fff
    style Manager fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

## ✅ Código Refatorado

```java
// Interfaces pequenas e específicas
public interface Codeable {
    void writeCode();
    void reviewCode();
}

public interface Deployable {
    void deployToProduction();
}

public interface Manageable {
    void manageTeam();
    void createBudget();
}

// Developer implementa apenas o que faz sentido
public class Developer implements Codeable, Deployable {
    @Override
    public void writeCode() {
        // implementação
    }
    
    @Override
    public void reviewCode() {
        // implementação
    }
    
    @Override
    public void deployToProduction() {
        // implementação
    }
}

// Manager implementa apenas o que faz sentido
public class Manager implements Manageable {
    @Override
    public void manageTeam() {
        // implementação
    }
    
    @Override
    public void createBudget() {
        // implementação
    }
}
```

---

## 🔍 Exemplo em Spring Boot: Repository

### ❌ Problema

```java
// Fat interface - força implementação de métodos não usados
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Herda 20+ métodos, mas usa apenas 3!
}
```

### ✅ Solução

```java
// Interface customizada com apenas o necessário
public interface ProductRepository extends Repository<Product, Long> {
    Product findById(Long id);
    List<Product> findAll();
    Product save(Product product);
}

// Ou use interfaces menores do Spring Data
public interface ReadOnlyProductRepository 
    extends Repository<Product, Long> {
    
    Product findById(Long id);
    List<Product> findAll();
}
```

---

## 📊 Comparação: Fat vs Segregated

```mermaid
graph TD
    subgraph "❌ Fat Interface"
        A[Interface<br/>20 métodos] --> B[Implementação A<br/>usa 5 métodos<br/>⚠️ 15 não usados]
        A --> C[Implementação B<br/>usa 8 métodos<br/>⚠️ 12 não usados]
    end
    
    subgraph "✅ Segregated Interfaces"
        D[Interface 1<br/>5 métodos] --> E[Implementação A<br/>usa 5 métodos<br/>✅ 100%]
        F[Interface 2<br/>8 métodos] --> G[Implementação B<br/>usa 8 métodos<br/>✅ 100%]
        H[Interface 3<br/>7 métodos] --> I[Implementação C<br/>usa 7 métodos<br/>✅ 100%]
    end
    
    style A fill:#f44336,stroke:#c62828,color:#fff
    style D fill:#4CAF50,stroke:#2E7D32,color:#fff
    style F fill:#4CAF50,stroke:#2E7D32,color:#fff
    style H fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

## 🎯 Benefícios do ISP

```mermaid
mindmap
  root((ISP))
    Flexibilidade
      Fácil adicionar novas implementações
      Menos código acoplado
      Reutilização focada
    Manutenibilidade
      Mudanças isoladas
      Interface clara
      Sem métodos não usados
    Testabilidade
      Mocks simples
      Testes focados
      Menos dependências
    Clareza
      Propósito claro
      Nomes descritivos
      Responsabilidade única
```

---

## 🚨 Sinais de Violação

```mermaid
flowchart TD
    A[Analisar Interface] --> B{Tem métodos<br/>não implementados?}
    B -->|Sim| X[❌ Viola ISP]
    B -->|Não| C{Implementações lançam<br/>UnsupportedOperation?}
    C -->|Sim| X
    C -->|Não| D{Métodos vazios ou<br/>com return null?}
    D -->|Sim| X
    D -->|Não| E{Cliente depende de<br/>métodos não usados?}
    E -->|Sim| X
    E -->|Não| F[✅ Respeita ISP]
    
    style F fill:#4CAF50,stroke:#2E7D32,color:#fff
    style X fill:#f44336,stroke:#c62828,color:#fff
```

---

## 🛠️ Como Refatorar

```mermaid
sequenceDiagram
    participant Old as Fat Interface
    participant Dev as Developer
    participant New as Segregated Interfaces
    
    Old->>Dev: Analyze usage
    Dev->>Dev: Identify client needs
    Dev->>New: Create specific interfaces
    Dev->>New: Move relevant methods
    Old->>Dev: Mark as @Deprecated
    Dev->>New: Migrate clients gradually
    Dev->>Old: Remove when no usage
```

---

## 💡 Dica do Instrutor

```
⚠️ Princípios de ISP:
1. Interfaces coesas: métodos relacionados juntos
2. Interfaces pequenas: 1-5 métodos idealmente
3. Múltiplas interfaces: cliente implementa as que precisa
4. Não force implementações vazias
5. Se tem @Deprecated ou throws UnsupportedOperation, refatore!

🎯 Regra prática:
- Se a implementação tem método vazio → Viola ISP
- Se o cliente não usa todos métodos → Viola ISP
```
