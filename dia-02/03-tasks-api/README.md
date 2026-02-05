# 📋 Tasks API - Exercício 2

## 📌 Objetivo
Evoluir uma API de Tarefas simples (memória) para usar **persistência em banco de dados** com Spring Data JPA.

## 🎯 Conceitos Trabalhados
- ✅ Migração de dados em memória para JPA
- ✅ **Paginação** e **Ordenação**
- ✅ **Query Methods** complexos
- ✅ **JPQL** para buscas customizadas
- ✅ **DTOs** para Request/Response
- ✅ **Validação** com Bean Validation
- ✅ **PATCH** para atualizações parciais
- ✅ Exception Handling global

---

## 🚀 Como Executar

### 1️⃣ Compilar e Rodar
```bash
cd dia-02/03-tasks-api
mvn clean install
mvn spring-boot:run
```

### 2️⃣ Acessar
- **API**: http://localhost:8082
- **H2 Console**: http://localhost:8082/h2-console
  - JDBC URL: `jdbc:h2:mem:tasksdb`
  - Username: `sa`
  - Password: *(vazio)*

### 3️⃣ Testar Endpoints
Use o arquivo `api-requests.http` no VS Code com a extensão **REST Client**.

---

## 📚 Estrutura do Projeto

```
03-tasks-api/
├── model/
│   ├── Task.java           # Entidade JPA com @PrePersist/@PreUpdate
│   └── Priority.java       # Enum (LOW, MEDIUM, HIGH, URGENT)
├── dto/
│   ├── CreateTaskRequest   # Record para criação
│   ├── UpdateTaskRequest   # Record para atualização (PATCH)
│   └── TaskResponse        # Record para resposta
├── repository/
│   └── TaskRepository      # Query Methods + JPQL + Busca Dinâmica
├── service/
│   └── TaskService         # Lógica de negócio + Transações
├── controller/
│   └── TaskController      # REST endpoints + Paginação
└── exception/
    ├── TaskNotFoundException
    └── GlobalExceptionHandler
```

---

## 🔍 Endpoints Principais

### **CRUD Básico**
```http
GET    /api/tasks              # Lista paginada
GET    /api/tasks/{id}         # Busca por ID
POST   /api/tasks              # Criar
PUT    /api/tasks/{id}         # Atualizar completo
PATCH  /api/tasks/{id}         # Atualização parcial ⭐
DELETE /api/tasks/{id}         # Deletar
```

### **Filtros e Buscas**
```http
GET /api/tasks/search?keyword=Spring&priority=HIGH&completed=false
GET /api/tasks/pending         # Tarefas pendentes
GET /api/tasks/completed       # Tarefas concluídas
GET /api/tasks/priority/URGENT # Filtrar por prioridade
GET /api/tasks/overdue         # Tarefas atrasadas
```

### **Ações Especiais**
```http
PATCH /api/tasks/{id}/complete  # Marcar como concluída
GET   /api/tasks/statistics     # Estatísticas (total, pendentes, concluídas)
```

### **Paginação e Ordenação**
```http
GET /api/tasks?page=0&size=10&sort=priority&direction=DESC
```

---

## 💡 Destaques Técnicos

### 1️⃣ **Query Methods vs JPQL**
```java
// Query Method (Spring Data gera automaticamente)
List<Task> findByPriority(Priority priority);

// JPQL personalizado
@Query("SELECT t FROM Task t WHERE t.priority = :priority AND t.completed = false")
List<Task> findPendingByPriority(@Param("priority") Priority priority);
```

### 2️⃣ **Busca Dinâmica com Múltiplos Filtros**
```java
@Query("SELECT t FROM Task t WHERE " +
       "(:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
       "(:priority IS NULL OR t.priority = :priority) AND " +
       "(:completed IS NULL OR t.completed = :completed)")
Page<Task> searchTasks(
    @Param("keyword") String keyword,
    @Param("priority") Priority priority,
    @Param("completed") Boolean completed,
    Pageable pageable
);
```

### 3️⃣ **PATCH vs PUT**
```java
// PUT - Atualização completa (todos os campos obrigatórios)
@PutMapping("/{id}")
public TaskResponse updateTask(@PathVariable Long id, @Valid @RequestBody UpdateTaskRequest request)

// PATCH - Atualização parcial (apenas campos enviados)
@PatchMapping("/{id}")
public TaskResponse partialUpdateTask(@PathVariable Long id, @RequestBody UpdateTaskRequest request)
```

### 4️⃣ **Método de Negócio na Entidade**
```java
public void complete() {
    this.completed = true;
    this.completedAt = LocalDateTime.now();
}
```

### 5️⃣ **Auditoria Automática**
```java
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = LocalDateTime.now();
}

@PreUpdate
protected void onUpdate() {
    updatedAt = LocalDateTime.now();
}
```

---

## 🎓 Exercícios Propostos

### Exercício 1: Criar Tarefas Urgentes
1. Crie 3 tarefas com prioridade `URGENT`
2. Liste todas as tarefas urgentes
3. Verifique quantas existem usando `/statistics`

### Exercício 2: Gerenciar Tarefas Atrasadas
1. Consulte `/api/tasks/overdue`
2. Marque uma tarefa atrasada como concluída usando `PATCH /api/tasks/{id}/complete`
3. Verifique novamente a lista de atrasadas

### Exercício 3: Busca Avançada
1. Busque tarefas que contenham "API" no título
2. Filtre por prioridade `HIGH` e status `completed=false`
3. Use paginação com `size=3`

### Exercício 4: Atualização Parcial
1. Crie uma tarefa com prioridade `LOW`
2. Use `PATCH` para atualizar **apenas** a prioridade para `URGENT`
3. Verifique que os demais campos não foram alterados

### Exercício 5: Validação
1. Tente criar uma tarefa sem título (deve retornar 400)
2. Tente buscar uma tarefa com ID inexistente (deve retornar 404)
3. Observe a estrutura das mensagens de erro

---

## 📊 Dados de Teste
O arquivo `data.sql` já contém 10 tarefas de exemplo com diferentes:
- Prioridades (LOW, MEDIUM, HIGH, URGENT)
- Status (pendentes e concluídas)
- Prazos (algumas atrasadas, outras futuras)

---

## 🔗 Diferenças do Dia 1 para Dia 2

| Aspecto | Dia 1 (Memória) | Dia 2 (JPA) |
|---------|----------------|-------------|
| Armazenamento | `ArrayList` | Banco H2 |
| Identificador | Manual (`UUID`) | `@GeneratedValue` |
| Busca | Loop manual | Query Methods |
| Paginação | Não tem | `Pageable` |
| Validação | Básica | `@Valid` + Bean Validation |
| Resposta | Entidade | DTOs (Records) |
| Exception | Try-catch | `@RestControllerAdvice` |

---

## 🧪 Testando Query Methods

### 1. Buscar Pendentes
```http
GET http://localhost:8082/api/tasks/pending
```

### 2. Buscar por Prioridade
```http
GET http://localhost:8082/api/tasks/priority/HIGH
```

### 3. Busca Dinâmica
```http
GET http://localhost:8082/api/tasks/search?keyword=Spring&priority=HIGH
```

### 4. Paginação
```http
GET http://localhost:8082/api/tasks?page=0&size=5&sort=dueDate&direction=ASC
```

---

## 🎯 Objetivos de Aprendizado
Ao finalizar este exercício, você será capaz de:
- ✅ Migrar uma API de memória para persistência JPA
- ✅ Implementar **paginação** e **ordenação**
- ✅ Criar **Query Methods** personalizados
- ✅ Escrever **JPQL** para consultas complexas
- ✅ Usar **DTOs** (Records) para Request/Response
- ✅ Implementar **validações** com Bean Validation
- ✅ Diferenciar **PUT** de **PATCH**
- ✅ Tratar exceções com `@RestControllerAdvice`

---

## 📝 Próximos Passos
1. Adicionar relacionamento `User` → `Task` (OneToMany)
2. Implementar autenticação com Spring Security
3. Migrar para PostgreSQL em produção
4. Adicionar testes unitários e de integração
5. Documentar com Swagger/OpenAPI
