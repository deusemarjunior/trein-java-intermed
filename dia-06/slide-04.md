# Slide 4: JPA N+1 — Soluções (JOIN FETCH, @EntityGraph)

**Horário:** 10:15 - 10:45

---

## Visão Geral das Soluções

```mermaid
graph TB
    PROBLEM["🔴 Problema N+1<br/>1 + N queries"]

    PROBLEM --> SOL1["Solução 1<br/><b>JOIN FETCH</b><br/>JPQL explícita"]
    PROBLEM --> SOL2["Solução 2<br/><b>@EntityGraph</b><br/>Declarativa"]
    PROBLEM --> SOL3["Solução 3<br/><b>Projeção DTO</b><br/>Slide 5"]
    PROBLEM --> SOL4["Solução 4<br/><b>Batch Fetch</b><br/>@BatchSize"]

    SOL1 --> R1["1 query com INNER JOIN"]
    SOL2 --> R2["1 query com LEFT JOIN"]
    SOL3 --> R3["1 query, só campos necessários"]
    SOL4 --> R4["1 + ceil(N/batchSize) queries"]

    style PROBLEM fill:#e74c3c,color:#fff
    style SOL1 fill:#3498db,color:#fff
    style SOL2 fill:#3498db,color:#fff
    style SOL3 fill:#2ecc71,color:#fff
    style SOL4 fill:#9b59b6,color:#fff
```

---

## Solução 1: JOIN FETCH na JPQL

O `JOIN FETCH` instrui o Hibernate a buscar o relacionamento **na mesma query**, usando um JOIN SQL.

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // ✅ Uma única query com JOIN
    @Query("SELECT e FROM Employee e JOIN FETCH e.department")
    List<Employee> findAllWithDepartment();
}
```

### SQL Gerado

```sql
-- UMA ÚNICA query com JOIN
SELECT e.id, e.name, e.email, e.salary,
       d.id, d.name, d.code
FROM employees e
INNER JOIN departments d ON e.department_id = d.id;
```

### Comparação Visual

```mermaid
sequenceDiagram
    participant App
    participant DB

    Note over App,DB: ❌ Sem JOIN FETCH (N+1)
    App->>DB: SELECT * FROM employees
    DB-->>App: 100 rows
    loop 100 vezes
        App->>DB: SELECT * FROM departments WHERE id=?
        DB-->>App: 1 row
    end
    Note over App,DB: Total: 101 queries, ~500ms

    Note over App,DB: ✅ Com JOIN FETCH (1 query)
    App->>DB: SELECT e.*, d.* FROM employees e JOIN departments d ON ...
    DB-->>App: 100 rows (com department embutido)
    Note over App,DB: Total: 1 query, ~5ms
```

---

## Solução 2: @EntityGraph

O `@EntityGraph` é uma forma **declarativa** de indicar quais relacionamentos carregar — sem escrever JPQL:

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    // ✅ Mesmo resultado, sem JPQL
    @EntityGraph(attributePaths = {"department"})
    List<Employee> findAll();
}
```

### SQL Gerado (idêntico ao JOIN FETCH)

```sql
SELECT e.id, e.name, e.email, e.salary,
       d.id, d.name, d.code
FROM employees e
LEFT JOIN departments d ON e.department_id = d.id;
```

> **Diferença sutil**: `@EntityGraph` gera `LEFT JOIN` (inclui funcionários sem departamento), `JOIN FETCH` gera `INNER JOIN` por padrão.

### JOIN FETCH vs @EntityGraph — Tipo de JOIN

```mermaid
graph LR
    subgraph "INNER JOIN (JOIN FETCH)"
        EI["Employees COM<br/>departamento ✅"]
        style EI fill:#2ecc71,color:#fff
    end

    subgraph "LEFT JOIN (@EntityGraph)"
        EL1["Employees COM<br/>departamento ✅"]
        EL2["Employees SEM<br/>departamento ✅"]
        style EL1 fill:#2ecc71,color:#fff
        style EL2 fill:#f39c12,color:#fff
    end
```

| Tipo de JOIN | Comportamento | Funcionários sem departamento |
|:---:|:---|:---:|
| `INNER JOIN` | Retorna apenas registros com match | ❌ Excluídos |
| `LEFT JOIN` | Retorna todos, com ou sem match | ✅ Incluídos (department = null) |

---

## Solução 3: @BatchSize (Batch Fetching)

Quando não pode usar JOIN FETCH (ex: coleções), o `@BatchSize` agrupa as queries lazy em lotes:

```java
@Entity
public class Department {

    @OneToMany(mappedBy = "department")
    @BatchSize(size = 10)   // ← Carrega 10 departamentos por vez
    private List<Employee> employees;
}
```

```mermaid
graph LR
    subgraph "❌ Sem @BatchSize (N queries)"
        Q1["SELECT * WHERE dept_id = 1"]
        Q2["SELECT * WHERE dept_id = 2"]
        Q3["SELECT * WHERE dept_id = 3"]
        QN["... +97 queries"]
    end

    subgraph "✅ Com @BatchSize(10) (ceil(N/10) queries)"
        B1["SELECT * WHERE dept_id IN (1,2,3,4,5,6,7,8,9,10)"]
        B2["SELECT * WHERE dept_id IN (11,12,13,14,15,16,17,18,19,20)"]
        BN["... 10 queries no total"]
    end

    style B1 fill:#2ecc71,color:#fff
    style Q1 fill:#e74c3c,color:#fff
```

> **@BatchSize** não elimina todas as queries extras, mas reduz drasticamente de N para N/batch. Útil para `@OneToMany` em coleções.

---

## JOIN FETCH vs. @EntityGraph — Quando usar cada um?

| Aspecto | `JOIN FETCH` (JPQL) | `@EntityGraph` | `@BatchSize` |
|---------|:---:|:---:|:---:|
| Precisa de JPQL? | Sim | Não | Não |
| Tipo de JOIN | INNER (padrão) | LEFT (padrão) | N/A (lazy em lote) |
| Condições extras na query | ✅ Sim (WHERE, ORDER BY) | ⚠️ Limitado | ❌ Não |
| Múltiplos relacionamentos | ✅ `JOIN FETCH a JOIN FETCH b` | ✅ `{"a", "b"}` | ✅ Separado por entidade |
| Combinável com query methods | ❌ | ✅ `findByNameContaining` | ✅ Na entidade |
| Legibilidade | JPQL pode ficar longo | Mais limpo | Mais simples |
| Melhor para | Queries customizadas | Queries derivadas | Coleções @OneToMany |

### Exemplo com múltiplos relacionamentos

```java
// JOIN FETCH — múltiplos
@Query("SELECT o FROM Order o " +
       "JOIN FETCH o.customer " +
       "JOIN FETCH o.items i " +
       "JOIN FETCH i.product")
List<Order> findAllWithDetails();

// @EntityGraph — múltiplos
@EntityGraph(attributePaths = {"customer", "items", "items.product"})
List<Order> findAll();
```

```mermaid
graph TD
    subgraph "Carregamento com EntityGraph"
        O["Order"]
        O --> C["customer<br/>(attributePath)"]
        O --> I["items<br/>(attributePath)"]
        I --> P["items.product<br/>(attributePath aninhado)"]
    end

    subgraph "SQL gerado (1 query)"
        SQL["SELECT o.*, c.*, i.*, p.*<br/>FROM orders o<br/>LEFT JOIN customers c ON ...<br/>LEFT JOIN order_items i ON ...<br/>LEFT JOIN products p ON ..."]
    end

    style SQL fill:#2ecc71,color:#fff
```

---

## ⚠️ Cuidado: JOIN FETCH com Paginação

**JOIN FETCH + Pageable NÃO funciona bem** — o Hibernate carrega tudo em memória para paginar:

```java
// ⚠️ Alerta! Hibernate carrega TUDO e pagina em memória
@Query("SELECT e FROM Employee e JOIN FETCH e.department")
Page<Employee> findAllWithDepartment(Pageable pageable);
```

Log de aviso:
```
HHH90003004: firstResult/maxResults specified with collection fetch; applying in memory
```

```mermaid
graph TD
    subgraph "⚠️ JOIN FETCH + Pageable"
        DB1[("Banco<br/>10.000 registros")]
        DB1 -->|"SELECT * JOIN FETCH"| MEM["JVM Memory<br/>10.000 objetos carregados!"]
        MEM -->|"Pagina em memória<br/>retorna 10"| RESP["Resposta: 10 itens"]
    end

    subgraph "✅ Projeção DTO + Pageable"
        DB2[("Banco<br/>10.000 registros")]
        DB2 -->|"SELECT ... LIMIT 10 OFFSET 0"| RESP2["Resposta: 10 itens<br/>Banco faz a paginação!"]
    end

    style MEM fill:#e74c3c,color:#fff
    style RESP2 fill:#2ecc71,color:#fff
```

### Soluções para paginação + relacionamento

**Opção 1**: Usar `@EntityGraph` com query method (funciona com Pageable)

```java
@EntityGraph(attributePaths = {"department"})
Page<Employee> findAll(Pageable pageable);
```

**Opção 2**: Usar **Projeção DTO** (próximo slide) — a melhor opção para listagens

```java
@Query("SELECT new com.example.dto.EmployeeSummary(e.id, e.name, d.name) " +
       "FROM Employee e JOIN e.department d")
Page<EmployeeSummary> findAllSummaries(Pageable pageable);
```

**Opção 3**: Duas queries (IDs paginados + fetch pelos IDs)

```java
// Query 1: buscar IDs paginados (leve, paginação no banco)
@Query("SELECT e.id FROM Employee e")
Page<Long> findAllIds(Pageable pageable);

// Query 2: buscar entidades completas pelos IDs (JOIN FETCH ok com lista)
@Query("SELECT e FROM Employee e JOIN FETCH e.department WHERE e.id IN :ids")
List<Employee> findAllByIdIn(@Param("ids") List<Long> ids);
```

---

## 🎯 Árvore de Decisão — Qual Solução Usar?

```mermaid
flowchart TD
    START["Preciso carregar<br/>relacionamento?"]
    START -->|"Não"| LAZY["✅ Mantenha LAZY<br/>Não acesse o campo"]
    START -->|"Sim"| TIPO["Tipo de consulta?"]

    TIPO -->|"Listagem sem paginação"| SIMPLES{"Query<br/>customizada?"}
    SIMPLES -->|"Sim (WHERE, etc)"| FETCH["✅ JOIN FETCH<br/>@Query + JPQL"]
    SIMPLES -->|"Não (findAll)"| EG["✅ @EntityGraph<br/>attributePaths"]

    TIPO -->|"Listagem paginada"| PAG{"Precisa da<br/>entity completa?"}
    PAG -->|"Não"| PROJ["🌟 Projeção DTO<br/>Record + Page"]
    PAG -->|"Sim"| TWO["Two Queries<br/>IDs + FETCH"]

    TIPO -->|"Detalhe (findById)"| DET["✅ @EntityGraph<br/>ou JOIN FETCH<br/>(1+1 é aceitável)"]

    TIPO -->|"Coleção @OneToMany"| BATCH["✅ @BatchSize<br/>Carrega em lotes"]

    style PROJ fill:#2ecc71,color:#fff
    style FETCH fill:#3498db,color:#fff
    style EG fill:#3498db,color:#fff
    style LAZY fill:#95a5a6,color:#fff
    style DET fill:#3498db,color:#fff
    style BATCH fill:#9b59b6,color:#fff
```

### Checklist de Performance JPA

- [ ] `spring.jpa.show-sql=true` ativado em dev (para detectar N+1)
- [ ] Todos os `@ManyToOne` e `@OneToMany` com `FetchType.LAZY`
- [ ] Listagens usam `JOIN FETCH` ou `@EntityGraph`
- [ ] Paginação usa `Pageable` + Projeção DTO
- [ ] Nenhuma query desnecessária nos logs
- [ ] `@BatchSize` configurado para coleções grandes

### Resumo em uma frase

| Solução | Quando | SQL |
|---------|--------|-----|
| **JOIN FETCH** | Query customizada sem paginação | `INNER JOIN` |
| **@EntityGraph** | Query derivada (findAll, findBy...) | `LEFT JOIN` |
| **Projeção DTO** | Listagem paginada (melhor prática) | `SELECT campos` |
| **@BatchSize** | Coleções @OneToMany | `WHERE id IN (...)` |
| **Two Queries** | Paginação + entity completa | 2 queries separadas |

> **Próximo**: Projeções DTO e Paginação — carregando apenas o necessário!
