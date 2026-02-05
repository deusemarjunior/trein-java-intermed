# Records Demo - Java 17+

Demonstração prática de **Java Records** introduzidos no Java 14 e finalizados no Java 16.

## 📝 O que são Records?

Records são classes especiais imutáveis que automaticamente geram:
- Constructor
- Getters (sem prefixo `get`)
- `equals()` e `hashCode()`
- `toString()`

## 🚀 Como executar

```bash
# Compilar
mvn clean compile

# Executar
mvn exec:java -Dexec.mainClass="com.example.records.RecordsDemo"
```

Ou usando Java diretamente:
```bash
javac -d target/classes src/main/java/com/example/records/*.java
java -cp target/classes com.example.records.RecordsDemo
```

## 🎯 Conceitos Demonstrados

1. **Criação de Record** - Sintaxe simples
2. **Getters automáticos** - Sem prefixo `get`
3. **Equals/HashCode** - Comparação automática por valor
4. **Métodos customizados** - `isExpensive()`, `applyDiscount()`
5. **Imutabilidade** - Criar novos objetos ao invés de modificar
6. **Compact Constructor** - Validação centralizada
7. **Factory Methods** - Métodos estáticos de criação

## 💡 Quando usar Records?

✅ **USE para:**
- DTOs (Data Transfer Objects)
- Value Objects
- Objetos imutáveis
- Respostas de API

❌ **NÃO USE para:**
- Entidades JPA (precisam de setters)
- Classes com lógica complexa
- Quando precisa herança
