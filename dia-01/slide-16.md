# Slide 16: Review e Q&A

**Horário:** 15:30 - 16:00

---

## ✅ O que aprendemos hoje

### Java Moderno (17/21)
- ✓ Records (DTOs imutáveis)
- ✓ Sealed Classes (hierarquias controladas)
- ✓ Text Blocks (strings multilinha)
- ✓ Pattern Matching (instanceof e switch)
- ✓ Stream API (programação funcional)
- ✓ Optional (lidar com null)

### Lombok vs Records
- ✓ Records são preferíveis para DTOs
- ✓ Lombok ainda útil para entidades JPA
- ✓ @Slf4j conveniente para logging

### Spring Boot
- ✓ IoC e DI (Inversion of Control, Dependency Injection)
- ✓ Auto-configuração
- ✓ Starters
- ✓ Profiles (dev, test, prod)

### Primeira API REST
- ✓ Controller (endpoints)
- ✓ Service (lógica de negócio)
- ✓ Repository (acesso a dados)
- ✓ Entity (modelo JPA)
- ✓ DTOs (Request/Response)
- ✓ Validação (@Valid)

---

## 🤔 Perguntas Comuns

**Q: Quando usar Records vs Classes?**  
A: Records para DTOs imutáveis. Classes para entidades JPA ou quando precisa mutabilidade.

**Q: @Autowired é obrigatório?**  
A: Não! Constructor injection não precisa (recomendado). Field/Setter injection precisam.

**Q: DDL-auto create-drop é seguro?**  
A: NUNCA em produção! Só dev/test. Use `validate` em prod.

**Q: Como debugar aplicação Spring?**  
A: Logs, breakpoints, Spring Boot Actuator (dia 9).

**Q: Preciso saber XML?**  
A: Não mais! Spring Boot usa annotations e YAML.

---

## 📝 Checklist de Aprendizado

```
[ ] Sei criar Records com validação
[ ] Entendo diferença entre Spring e Spring Boot
[ ] Sei o que é IoC e DI
[ ] Consigo criar projeto no Spring Initializr
[ ] Entendo estrutura de pastas do projeto
[ ] Sei criar Entity, Repository, Service, Controller
[ ] Entendo anotações básicas (@RestController, @Service, etc)
[ ] Consigo testar API com Postman
[ ] Sei configurar profiles
```

---

## 🏠 Tarefa de Casa

### 1. Completar Exercício 2 (API de Tarefas)
- Todos os endpoints funcionando
- Testes com Postman documentados

### 2. Estender a API de Tarefas
```java
// Adicionar enum Priority
public enum Priority { LOW, MEDIUM, HIGH }

// Novo endpoint
GET /api/tasks/search?status=completed&priority=HIGH
```

### 3. Preparação para Dia 2
- Instalar PostgreSQL ou ter Docker pronto
- Revisar SQL básico (SELECT, INSERT, UPDATE, DELETE)

---

## 📚 Leitura Recomendada

- [ ] Spring Boot Reference Documentation (Seções 1-3)
- [ ] Java Records Tutorial
- [ ] Effective Java - Item 16
- [ ] Modern Java in Action - Chapter 3

---

## 🎉 Parabéns!

Você completou o Dia 1!

Amanhã: **Spring Data JPA, DTOs, Exception Handling**
