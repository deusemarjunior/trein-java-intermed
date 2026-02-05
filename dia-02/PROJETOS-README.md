# 📚 Projetos Java - Dia 02

## 📖 Ordem de Estudo Recomendada

### 1️⃣ **01-spring-data-jpa-demo** (Projeto Completo - Demonstração)
**Objetivo**: Projeto completo demonstrando todos os conceitos de Spring Data JPA integrados.

**Conceitos**:
- Entidades JPA com relacionamentos (OneToOne, OneToMany, ManyToMany)
- Query Methods, JPQL e Native SQL
- Paginação e Ordenação
- DTOs e conversão de entidades
- Validação com Bean Validation
- Exception Handling global
- Múltiplos perfis (dev/prod)

**Porta**: 8080  
**Arquivo de Testes**: `api-requests.http`

---

### 2️⃣ **02-blog-api** (Exercício 1: Relacionamentos JPA)
**Objetivo**: Prática focada em relacionamentos JPA e problema N+1.

**Conceitos**:
- Relacionamentos bidirecionais (OneToMany, ManyToMany)
- Helpers para sincronização
- JOIN FETCH vs Lazy Loading
- **Problema N+1** e sua solução
- FetchType.LAZY vs EAGER

**Porta**: 8081  
**Arquivo de Testes**: `api-requests.http`  
**Destaque**: Compare `findById()` vs `findByIdWithDetails()` nos logs SQL

---

### 3️⃣ **03-tasks-api** (Exercício 2: Migração Memória → JPA)
**Objetivo**: Evoluir uma API de Tarefas de memória para persistência em banco de dados.

**Conceitos**:
- Migração de `ArrayList` para JPA Repository
- **Paginação** com `Pageable`
- **Query Methods** complexos
- **JPQL** para buscas dinâmicas
- **DTOs** (Records) para Request/Response
- **PATCH** vs **PUT** (atualização parcial vs completa)
- Auditoria com `@PrePersist` e `@PreUpdate`

**Porta**: 8082  
**Arquivo de Testes**: `api-requests.http`  
**Destaque**: Busca dinâmica com múltiplos filtros opcionais

---

## 🚀 Como Usar

### 1. **Estude primeiro o projeto completo** (01-spring-data-jpa-demo)
   - Execute: `cd 01-spring-data-jpa-demo && mvn spring-boot:run`
   - Teste todos os endpoints usando `api-requests.http`
   - Observe os logs SQL no console
   - Acesse o H2 Console: http://localhost:8080/h2-console

### 2. **Pratique com os exercícios focados**:
   
   **Exercício 1 - Blog API (Relacionamentos)**
   ```bash
   cd 02-blog-api
   mvn spring-boot:run
   ```
   - Compare as consultas N+1 vs JOIN FETCH
   - Observe a diferença nos logs SQL
   - Teste criação de posts com comentários e tags

   **Exercício 2 - Tasks API (Paginação e Query Methods)**
   ```bash
   cd 03-tasks-api
   mvn spring-boot:run
   ```
   - Teste paginação e ordenação
   - Use filtros de busca dinâmica
   - Compare PATCH vs PUT
   - Pratique Query Methods e JPQL

### 3. **Consulte os slides** para teoria complementar
   - `slide-05.md` - Fundamentos JPA
   - `slide-06.md` - Relacionamentos
   - `slide-07.md` - Spring Data JPA Repositories
   - `slide-08.md` - Query Methods e JPQL
   - `slide-09.md` - Paginação e Ordenação

---

## 📊 Comparação dos Projetos

| Aspecto | 01-demo | 02-blog-api | 03-tasks-api |
|---------|---------|-------------|--------------|
| **Foco** | Completo | Relacionamentos | Paginação/Queries |
| **Entidades** | 7 | 3 | 1 |
| **Relacionamentos** | ✅ Todos | ✅ Foco principal | ❌ Nenhum |
| **N+1 Problem** | ✅ | ✅ Demonstração | - |
| **Paginação** | ✅ | ❌ | ✅ Foco principal |
| **Query Methods** | ✅ Básico | ✅ Com JOIN FETCH | ✅ Complexos |
| **JPQL** | ✅ | ✅ | ✅ Busca dinâmica |
| **PATCH** | ❌ | ❌ | ✅ Demonstração |
| **Nível** | Intermediário | Iniciante | Intermediário |

---

## 📌 Informações Técnicas

### Portas dos Projetos
- **01-spring-data-jpa-demo**: http://localhost:8080
- **02-blog-api**: http://localhost:8081
- **03-tasks-api**: http://localhost:8082

### H2 Console
Todos os projetos têm H2 Console habilitado:
- **URL Console**: http://localhost:{PORT}/h2-console
- **JDBC URL**: 
  - Demo: `jdbc:h2:mem:demodb`
  - Blog: `jdbc:h2:mem:blogdb`
  - Tasks: `jdbc:h2:mem:tasksdb`
- **Username**: `sa`
- **Password**: *(vazio)*

### Dados de Teste
Todos os projetos carregam dados automaticamente via `data.sql`:
- **01-demo**: 30+ registros (produtos, categorias, posts, usuários, etc.)
- **02-blog-api**: 15+ registros (posts, comentários, tags)
- **03-tasks-api**: 10 tarefas com diferentes prioridades e status

---

## 🎯 Objetivos de Aprendizado

Ao completar os 3 projetos, você dominará:
- ✅ Configuração de entidades JPA
- ✅ Relacionamentos (OneToOne, OneToMany, ManyToMany)
- ✅ Sincronização bidirecional
- ✅ Query Methods (convenções de nomenclatura)
- ✅ JPQL e Native SQL
- ✅ Paginação e Ordenação
- ✅ DTOs e conversão de entidades
- ✅ Validação com Bean Validation
- ✅ Exception Handling global
- ✅ Diferenças entre PUT e PATCH
- ✅ Problema N+1 e soluções
- ✅ FetchType.LAZY vs EAGER
- ✅ Auditoria de entidades

---

## 💡 Dicas de Estudo

1. **Sempre observe os logs SQL** - Configure `show-sql: true` para ver as queries executadas
2. **Use o H2 Console** - Visualize os dados diretamente no banco
3. **Compare as abordagens** - Veja a diferença entre Query Methods, JPQL e Native SQL
4. **Teste os exemplos** - Use os arquivos `api-requests.http` para testar todos os endpoints
5. **Modifique os dados** - Crie seus próprios cenários de teste
6. **Consulte os READMEs** - Cada projeto tem documentação específica

---

## 📖 Próximos Passos

Após dominar estes conceitos:
1. Adicionar Spring Security (autenticação/autorização)
2. Migrar para PostgreSQL em produção
3. Implementar testes unitários e de integração
4. Adicionar cache com Redis
5. Documentar APIs com Swagger/OpenAPI
6. Implementar CI/CD com GitHub Actions

---

**Bons estudos! 🎓**
