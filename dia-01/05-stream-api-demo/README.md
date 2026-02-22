# Stream API & Optional Demo - Java 8+

Demonstração completa da **Stream API** e **Optional** do Java.

## 📝 O que é Stream API?

Stream API permite processar coleções de forma funcional e declarativa, usando operações como filter, map, reduce, etc.

## 🎯 Conceitos Demonstrados

### 1. Imperativo vs Funcional
Comparação entre código tradicional (loops) e Stream API.

### 2. Operações Comuns
- **filter**: Filtrar elementos
- **map**: Transformar elementos
- **sorted**: Ordenar
- **distinct**: Remover duplicatas
- **limit/skip**: Paginação

### 3. Agregações
- **count**: Contar elementos
- **reduce**: Somar/combinar
- **min/max**: Menor/maior
- **average**: Média
- **anyMatch/allMatch/noneMatch**: Verificações

### 4. Agrupamento
- **groupingBy**: Agrupar por propriedade
- **partitioningBy**: Dividir em 2 grupos
- **counting**: Contar por grupo

### 5. Optional
- **ifPresent**: Executar se presente
- **orElse**: Valor padrão
- **orElseGet**: Valor padrão lazy
- **orElseThrow**: Lançar exceção
- **map/filter**: Transformações

## 🚀 Como executar

```bash
# Compilar
mvn clean compile

# Executar
mvn exec:java -Dexec.mainClass="com.example.stream.StreamApiDemo"
```

Ou usando Java diretamente:
```bash
javac -d target/classes src/main/java/com/example/stream/*.java
java -cp target/classes com.example.stream.StreamApiDemo
```

## 💡 Operações de Stream

### Intermediárias (lazy)
- `filter()`
- `map()`
- `sorted()`
- `distinct()`
- `limit()`, `skip()`

### Terminais (eager)
- `forEach()`
- `collect()`
- `reduce()`
- `count()`
- `anyMatch()`, `allMatch()`, `noneMatch()`
- `findFirst()`, `findAny()`
- `min()`, `max()`

## ✅ Quando usar?

**Stream API:**
- Processamento de listas/coleções
- Transformações de dados
- Filtragens complexas
- Operações funcionais

**Optional:**
- Evitar NullPointerException
- APIs que podem retornar null
- Tornar intenção explícita

## ⚠️ Cuidados

- Streams são de uso único
- Operações são lazy (só executam no terminal)
- Evite side effects (modificar estado externo)
- Use parallel() com cautela
