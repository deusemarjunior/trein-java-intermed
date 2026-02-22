# Slide 3: JPA N+1 — O Problema e Diagnóstico

**Horário:** 09:45 - 10:15

---

## O que é o Problema N+1?

O problema N+1 é a **armadilha de performance mais comum do JPA/Hibernate**. Acontece quando uma query inicial gera N queries adicionais para carregar relacionamentos.

```mermaid
sequenceDiagram
    participant App as Aplicação
    participant Hibernate as Hibernate (L2)
    participant DB as Banco de Dados

    App->>Hibernate: employeeRepository.findAll()
    Hibernate->>DB: SELECT * FROM employees (1 query)
    DB-->>Hibernate: 100 funcionários

    Note over App,DB: Ao acessar emp.getDepartment().getName()...

    loop Para cada funcionário (N vezes)
        App->>Hibernate: emp.getDepartment().getName()
        Hibernate->>DB: SELECT * FROM departments WHERE id = ? (N queries)
        DB-->>Hibernate: 1 departamento
    end

    Note over App,DB: Total: 1 + 100 = 101 queries! 😱
```

---

## 🧩 Modelo de Dados — Relacionamento Employee ↔ Department

```mermaid
classDiagram
    class Employee {
        -Long id
        -String name
        -String email
        -String cpf
        -BigDecimal salary
        -Department department
        -LocalDateTime createdAt
        +getName() String
        +getDepartment() Department
    }

    class Department {
        -Long id
        -String name
        -String code
        -List~Employee~ employees
        +getName() String
        +getEmployees() List~Employee~
    }

    Employee "*" --> "1" Department : @ManyToOne(LAZY)
    Department "1" --> "*" Employee : @OneToMany(mappedBy)
```

> **@ManyToOne**: Vários funcionários pertencem a um departamento. O JPA cria a coluna `department_id` na tabela `employees`.

---

## Por que acontece? — Lazy Loading Explicado

O JPA usa **Lazy Loading** por padrão para `@ManyToOne` e `@OneToMany`. Isso significa que o relacionamento **só é carregado quando acessado**:

```mermaid
stateDiagram-v2
    [*] --> Proxy: findAll() retorna Employee com proxy
    Proxy --> Carregando: getDepartment().getName() chamado
    Carregando --> Carregado: Hibernate executa SELECT
    Carregado --> [*]: Dado disponível

    note right of Proxy
        department é um "proxy" Hibernate
        Não é o objeto real — é um placeholder
        Só busca no banco quando acessado
    end note
```

```java
@Entity
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @ManyToOne(fetch = FetchType.LAZY)  // ← Padrão para @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;     // ← É um PROXY, não o objeto real!
}
```

### O que é um Proxy Hibernate?

```mermaid
graph TB
    subgraph "Quando findAll() retorna"
        E1["Employee (id=1)<br/>name='Ana'<br/>department = <b>Proxy$$Department</b>"]
        E2["Employee (id=2)<br/>name='Bruno'<br/>department = <b>Proxy$$Department</b>"]
        E3["Employee (id=3)<br/>name='Carlos'<br/>department = <b>Proxy$$Department</b>"]
    end

    subgraph "Ao acessar getDepartment().getName()"
        P1["Proxy detecta acesso"] --> Q1["SELECT * FROM departments WHERE id=?"]
        Q1 --> D1["Department (id=1, name='Engenharia')"]
    end

    E1 -->|"getDepartment()"| P1

    style E1 fill:#3498db,color:#fff
    style P1 fill:#e74c3c,color:#fff
    style Q1 fill:#e74c3c,color:#fff
```

> **Proxy**: O Hibernate cria uma subclasse da entity em tempo de execução. Quando você chama qualquer método nesse proxy, ele intercepta e faz o SELECT no banco. **Isso é transparente** — você não percebe que é um proxy até olhar os logs SQL.

### O código que causa N+1

```java
@GetMapping
public List<EmployeeResponse> findAll() {
    List<Employee> employees = employeeRepository.findAll(); // 1 query

    return employees.stream()
        .map(emp -> new EmployeeResponse(
            emp.getId(),
            emp.getName(),
            emp.getDepartment().getName()  // ← Cada chamada dispara 1 query!
        ))
        .toList();
}
```

---

## 🔍 Diagnosticando o N+1

### Como ver na prática

Ative os logs SQL no `application.yml`:

```yaml
spring:
  jpa:
    show-sql: true
    properties:
      hibernate:
        format_sql: true

# Opcional: ver parâmetros das queries (valores dos ?)
logging:
  level:
    org.hibernate.SQL: DEBUG
    org.hibernate.type.descriptor.sql.BasicBinder: TRACE
```

### Fluxo de diagnóstico

```mermaid
flowchart TD
    A["Suspeita de N+1?"] --> B["Ativar show-sql: true"]
    B --> C["Fazer request GET /api/employees"]
    C --> D["Contar queries no log"]
    D --> E{"Mais queries<br/>que o esperado?"}
    E -->|"Sim"| F["🔴 N+1 detectado!<br/>Ver próximo slide para soluções"]
    E -->|"Não"| G["✅ Performance OK"]

    F --> H["Procurar padrão:<br/>1 SELECT principal +<br/>N SELECTs iguais com WHERE id=?"]

    style F fill:#e74c3c,color:#fff
    style G fill:#2ecc71,color:#fff
```

### O que aparece no log

```sql
-- Query 1: buscar todos os funcionários
Hibernate:
    select e1_0.id, e1_0.name, e1_0.email, e1_0.department_id
    from employees e1_0

-- Query 2: buscar departamento do funcionário 1
Hibernate:
    select d1_0.id, d1_0.name, d1_0.code
    from departments d1_0
    where d1_0.id = ?

-- Query 3: buscar departamento do funcionário 2
Hibernate:
    select d1_0.id, d1_0.name, d1_0.code
    from departments d1_0
    where d1_0.id = ?

-- ... repete para CADA funcionário!
```

---

## 📊 Impacto na Performance

```mermaid
graph LR
    subgraph "Crescimento das Queries"
        R10["10 registros<br/>11 queries<br/>~50ms"]
        R100["100 registros<br/>101 queries<br/>~500ms"]
        R1K["1.000 registros<br/>1.001 queries<br/>~5 seg ⚠️"]
        R10K["10.000 registros<br/>10.001 queries<br/>~50 seg 🔴"]
    end

    R10 --> R100 --> R1K --> R10K

    style R1K fill:#f39c12,color:#fff
    style R10K fill:#e74c3c,color:#fff
```

| Funcionários | Queries sem N+1 | Queries com N+1 | Tempo estimado | Impacto |
|:---:|:---:|:---:|:---:|:---|
| 10 | 1 | 11 | ~50ms | Imperceptível |
| 100 | 1 | 101 | ~500ms | Lento para o usuário |
| 1.000 | 1 | 1.001 | ~5 segundos | API timeout, UX ruim |
| 10.000 | 1 | 10.001 | ~50 segundos | Indisponibilidade total |

> **Em produção**: uma API que deveria responder em 50ms pode levar 5 segundos com N+1 em tabelas grandes. Já vi isso causar **incidentes em produção** em empresas grandes.

---

## Quando o N+1 acontece?

| Cenário | Acontece N+1? | Por quê |
|---------|:---:|---------|
| `findAll()` + acesso ao relacionamento | ✅ | Lazy loading dispara query por registro |
| `findById()` + acesso ao relacionamento | ⚠️ | Apenas 1 query extra (1+1), menos grave |
| `findAll()` sem acessar relacionamento | ❌ | Lazy loading nunca é disparado |
| `findAll()` com `FetchType.EAGER` | ⚠️ | Carrega sempre, pode ser pior que lazy |
| JPQL com `JOIN FETCH` | ❌ | Relacionamento vem no JOIN |
| `@EntityGraph` | ❌ | Hibernate faz LEFT JOIN |

### A "solução" errada: `FetchType.EAGER`

```java
// ❌ NÃO faça isso!
@ManyToOne(fetch = FetchType.EAGER)
private Department department;
```

```mermaid
graph TD
    subgraph "FetchType.EAGER — Problemas"
        A["Toda query carrega department"] --> B["Mesmo quando não precisa"]
        B --> C["Cascatear: Department → Company → Address"]
        C --> D["Perde controle do que é carregado"]
        D --> E["Performance imprevisível"]
    end

    style A fill:#e74c3c,color:#fff
    style E fill:#e74c3c,color:#fff
```

**Problemas do EAGER global**:
- Carrega o relacionamento **em todas as queries**, mesmo quando desnecessário
- Pode cascatear: Department → Company → Address → Country...
- Perde o controle de quais dados são carregados
- **N+1 continua existindo** (apenas muda de lazy para eager, mas as queries extras persistem em coleções)

> **Best practice**: Mantenha `LAZY` e use `JOIN FETCH` ou `@EntityGraph` **onde precisa**.

---

## 🧪 Exercício Mental

Dado o modelo:

```java
@Entity
public class Order {
    @ManyToOne private Customer customer;
    @OneToMany private List<OrderItem> items;
}

@Entity
public class OrderItem {
    @ManyToOne private Product product;
}
```

```mermaid
classDiagram
    Order "*" --> "1" Customer : @ManyToOne
    Order "1" --> "*" OrderItem : @OneToMany
    OrderItem "*" --> "1" Product : @ManyToOne
```

**Pergunta**: Quantas queries são geradas para listar 50 pedidos com customer, items e product?

<details>
<summary>🤔 Clique para ver a resposta</summary>

```mermaid
graph TD
    Q1["1 query: SELECT * FROM orders"]
    Q2["50 queries: SELECT * FROM customers WHERE id = ?<br/>(1 por pedido)"]
    Q3["50 queries: SELECT * FROM order_items WHERE order_id = ?<br/>(1 por pedido)"]
    Q4["~150 queries: SELECT * FROM products WHERE id = ?<br/>(~3 items por pedido)"]
    TOTAL["Total: 1 + 50 + 50 + 150 = 251 queries! 😱"]

    Q1 --> Q2 --> Q3 --> Q4 --> TOTAL

    style TOTAL fill:#e74c3c,color:#fff
```

- 1 query para buscar 50 pedidos
- 50 queries para buscar o customer de cada pedido
- 50 queries para buscar items de cada pedido
- Para cada pedido com ~3 items: 150 queries para buscar products

**Total: 1 + 50 + 50 + 150 = 251 queries!** 😱

**Com JOIN FETCH**: apenas **1 query** com JOINs aninhados.

</details>

> **No próximo slide**: como resolver isso com JOIN FETCH e @EntityGraph.
