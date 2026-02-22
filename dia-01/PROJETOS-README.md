# Dia 1 - Fundamentos Java Moderno, Servlets e JDBC

Projetos práticos com código funcional para todos os conceitos abordados nos slides do Dia 1.

## 📚 Projetos Disponíveis

### 1. Records Demo
**Pasta:** `01-records-demo/`  
**Conceitos:** Records, validação, imutabilidade, factory methods

Records do Java 21+ substituem DTOs tradicionais, eliminando código boilerplate.

```bash
cd 01-records-demo
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.records.RecordsDemo"
```

---

### 2. Sealed Classes Demo
**Pasta:** `02-sealed-classes-demo/`  
**Conceitos:** Sealed classes, hierarquia controlada, pattern matching

Sealed classes permitem controlar quais classes podem estender uma classe base.

```bash
cd 02-sealed-classes-demo
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.sealed.SealedClassesDemo"
```

---

### 3. Text Blocks Demo
**Pasta:** `03-text-blocks-demo/`  
**Conceitos:** Text blocks, strings multilinha, formatação

Text blocks facilitam trabalhar com JSON, SQL, HTML sem concatenações.

```bash
cd 03-text-blocks-demo
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.textblocks.TextBlocksDemo"
```

---

### 4. Pattern Matching Demo
**Pasta:** `04-pattern-matching-demo/`  
**Conceitos:** Pattern matching, switch expressions, guards  
**Requisito:** Java 21 (para todos os recursos)

Pattern matching elimina casts e torna código mais expressivo.

```bash
cd 04-pattern-matching-demo
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.pattern.PatternMatchingDemo"
```

---

### 5. Stream API Demo
**Pasta:** `05-stream-api-demo/`  
**Conceitos:** Stream API, Optional, programação funcional

Stream API para processamento funcional de coleções.

```bash
cd 05-stream-api-demo
mvn clean compile
mvn exec:java -Dexec.mainClass="com.example.stream.StreamApiDemo"
```

---

### 6. Products API - Servlet + JDBC ⭐
**Pasta:** `06-products-api/`  
**Conceitos:** Servlet, JDBC, DAO Pattern, Tomcat Embedded, REST API, Gson

API REST completa usando Servlet puro e JDBC (sem Spring Boot!).

```bash
cd 06-products-api
mvn clean compile exec:java -Dexec.mainClass="com.example.products.ProductsApp"
```

**Endpoints:** http://localhost:8080/api/products  
**Arquivo de testes:** `06-products-api/api-requests.http`

---

## 🎯 Ordem Recomendada de Estudo

1. **Records Demo** - Base para DTOs modernos
2. **Sealed Classes Demo** - Hierarquias controladas
3. **Text Blocks Demo** - Strings multilinha
4. **Pattern Matching Demo** - Switch moderno
5. **Stream API Demo** - Processamento funcional
6. **Products API** - API REST com Servlet + JDBC (integração de todos os conceitos)

## 🔧 Pré-requisitos

- **Java 21 ou superior**
- **Maven 3.8+**
- **IDE** (IntelliJ IDEA, VS Code, Eclipse)

### Verificar instalação

```bash
java -version   # Deve mostrar 21 ou superior
mvn -version    # Deve mostrar 3.8 ou superior
```

## 🚀 Executando os Projetos

Cada projeto tem seu próprio README com instruções detalhadas. 

### Forma rápida (todos os projetos):

```bash
# Records
cd 01-records-demo && mvn clean compile exec:java -Dexec.mainClass="com.example.records.RecordsDemo" && cd ..

# Sealed Classes
cd 02-sealed-classes-demo && mvn clean compile exec:java -Dexec.mainClass="com.example.sealed.SealedClassesDemo" && cd ..

# Text Blocks
cd 03-text-blocks-demo && mvn clean compile exec:java -Dexec.mainClass="com.example.textblocks.TextBlocksDemo" && cd ..

# Pattern Matching (requer Java 21)
cd 04-pattern-matching-demo && mvn clean compile exec:java -Dexec.mainClass="com.example.pattern.PatternMatchingDemo" && cd ..

# Stream API
cd 05-stream-api-demo && mvn clean compile exec:java -Dexec.mainClass="com.example.stream.StreamApiDemo" && cd ..

# Products API (Servlet + JDBC)
cd 06-products-api && mvn clean compile exec:java -Dexec.mainClass="com.example.products.ProductsApp"
```

## 📖 Material de Referência

Cada projeto demonstra conceitos específicos dos slides:

- **Slide 3:** Records → `01-records-demo`
- **Slide 4:** Sealed Classes → `02-sealed-classes-demo`
- **Slide 5:** Text Blocks → `03-text-blocks-demo`
- **Slide 6:** Pattern Matching → `04-pattern-matching-demo`
- **Slide 7:** Stream API → `05-stream-api-demo`
- **Slides 9-13:** Servlet + JDBC → `06-products-api`

## 💡 Dicas para os Alunos

### Explorando o Código

1. **Leia os comentários** - Todo código está documentado
2. **Execute e observe** - Veja a saída no console
3. **Modifique e teste** - Experimente mudanças
4. **Compare com slides** - Relacione com teoria

### Testando a API (Projeto 6)

Use **Postman**, **Insomnia**, **cURL** ou **REST Client do VS Code**:

```bash
# Ver todos os produtos
curl http://localhost:8080/api/products

# Criar produto
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Mouse","description":"Gaming","price":150,"category":"Electronics"}'
```

Arquivo com exemplos: `06-products-api/api-requests.http`

## 🐛 Troubleshooting

### Erro: "Java version mismatch"
```bash
# Verificar versão
java -version

# Atualizar JAVA_HOME se necessário
export JAVA_HOME=/path/to/java21
```

### Erro: "Port 8080 already in use"
```bash
# Matar processo na porta 8080
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Erro: Maven não encontrado
```bash
mvn -version
# Se necessário, instalar Maven
```
```bash
# Use Maven Wrapper (incluído em cada projeto)
./mvnw clean install  # Linux/Mac
mvnw.cmd clean install  # Windows
```

## 📚 Próximos Passos

Após completar os projetos do Dia 1:

- [ ] Revisar conceitos com os slides
- [ ] Modificar código para experimentar
- [ ] Criar seu próprio projeto combinando conceitos
- [ ] Preparar para Dia 2: JPA avançado, DTOs, Exception Handling

## 🎓 Recursos Adicionais

- [Java Records Tutorial](https://docs.oracle.com/en/java/javase/21/language/records.html)
- [Spring Boot Reference](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Stream API Guide](https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/util/stream/package-summary.html)

---

**Bom estudo! 🚀**

Se tiver dúvidas, consulte o README de cada projeto individual.
