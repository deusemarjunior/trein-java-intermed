# Slide 19: Coffee Break ☕

**Horário:** 14:20 - 15:00

---

## ☕ Pausa para o Café

**Aproveite para:**

- ☕ Tomar um café
- 🚶 Esticar as pernas
- 🤔 Revisar conceitos
- 💬 Tirar dúvidas informais
- 📱 Checar mensagens

---

## 📝 Revisão Rápida - Manhã

### ✅ O que vimos até agora:

**09:00 - 10:00 | Review & Setup**
- Configuração PostgreSQL + Docker
- Dependencies Spring Data JPA

**10:00 - 11:00 | HTTP & REST Avançado**
- Status codes e semântica HTTP
- Request/Response handling
- Validações

**11:00 - 12:00 | Exception Handling & JPA**
- @ControllerAdvice
- Entities e relacionamentos
- Cascade, FetchType, N+1 problem

**13:00 - 14:00 | Repositories & Queries**
- Spring Data JPA repositories
- Query methods
- JPQL vs SQL nativo
- Paginação

**14:00 - 14:20 | DTOs**
- Request vs Response
- Factory methods
- Validações

---

## 🎯 O que vem a seguir (15:00 - 16:30)

### Parte Prática - Exercício Blog API

**Você vai criar:**
- API completa de Blog
- Entities: Post, Comment, Category, Tag
- Relacionamentos @OneToMany e @ManyToMany
- Repositories com query methods customizados
- DTOs validados
- Paginação e busca
- Exception handling global

**Critérios de sucesso:**
- [ ] CRUD completo de Posts
- [ ] Comentários vinculados a Posts
- [ ] Busca por keyword e categoria
- [ ] Paginação funcionando
- [ ] Tratamento de erros

---

## 💡 Dicas para o Exercício

### 1. Comece pelo modelo
Desenhe as entidades e relacionamentos no papel primeiro!

### 2. Incremental
Não tente fazer tudo de uma vez:
1. Entities básicas (Post, Category)
2. Repositories
3. DTOs
4. Controllers simples
5. Adicione relacionamentos
6. Refine queries e validações

### 3. Teste constantemente
Use Postman para testar cada endpoint conforme cria.

### 4. Use os exemplos
Relembre os códigos que fizemos nos slides anteriores!

---

## 🤔 Perguntas Frequentes

**P: Devo usar Records para tudo?**  
R: Para DTOs sim! Para Entities, use classes normais com JPA.

**P: Quando usar JPQL vs Native SQL?**  
R: Use JPQL sempre que possível. Native SQL apenas para queries muito específicas do banco.

**P: Como evitar N+1?**  
R: Use `JOIN FETCH` em JPQL ou `@EntityGraph` em query methods.

**P: Qual FetchType usar?**  
R: Padrão LAZY para coleções. Carregue dados sob demanda com JOIN FETCH.

**P: Preciso criar DTO para tudo?**  
R: Para APIs REST, sim! Nunca exponha entidades diretamente.

---

## 📚 Links Úteis

- [Spring Data JPA Reference](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [JPQL Language Reference](https://docs.oracle.com/javaee/7/tutorial/persistence-querylanguage.htm)
- [Bean Validation Constraints](https://beanvalidation.org/2.0/spec/#builtinconstraints)
- [PostgreSQL Documentation](https://www.postgresql.org/docs/)

---

## ⏰ Retorno: 15:00

Voltamos em **15 minutos** para o exercício prático!

Prepare seu ambiente:
- PostgreSQL rodando
- Postman aberto
- IDE pronta
- Café na mão ☕

**Próximo:** Exercício Blog API →
