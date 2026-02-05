# Pattern Matching Demo - Java 21

Demonstração de **Pattern Matching** e **Switch Expressions** do Java moderno.

## 📝 O que é Pattern Matching?

Pattern matching permite extrair e usar valores de objetos de forma mais concisa, eliminando casts manuais e verificações repetitivas.

## 🎯 Recursos Demonstrados

### 1. Pattern Matching for instanceof (Java 16+)
```java
// Antes
if (obj instanceof String) {
    String s = (String) obj;
    System.out.println(s.length());
}

// Agora
if (obj instanceof String s) {
    System.out.println(s.length());
}
```

### 2. Switch Expressions (Java 14+)
```java
String msg = switch (status) {
    case PENDING -> "Pendente";
    case COMPLETED -> "Concluído";
};
```

### 3. Pattern Matching for Switch (Java 21+)
```java
String desc = switch (obj) {
    case String s -> "String: " + s;
    case Integer i when i > 0 -> "Positive: " + i;
    case null -> "Null value";
    default -> "Other";
};
```

## 🚀 Como executar

```bash
# Compilar
mvn clean compile

# Executar
mvn exec:java -Dexec.mainClass="com.example.pattern.PatternMatchingDemo"
```

Ou usando Java diretamente:
```bash
javac -d target/classes src/main/java/com/example/pattern/*.java
java -cp target/classes com.example.pattern.PatternMatchingDemo
```

## ⚠️ Requisitos

- **Java 21** para todos os recursos (especialmente pattern matching for switch)
- Java 17 funciona mas sem pattern matching for switch completo

## 💡 Conceitos

1. **Pattern Matching for instanceof** - Elimina cast duplicado
2. **Switch Expressions** - Switch como expressão que retorna valor
3. **Yield** - Retornar valor em blocos de switch
4. **Guards** - Condições com `when`
5. **Null handling** - Tratamento de null no switch

## ✅ Benefícios

- Código mais conciso
- Menos erros de cast
- Expressivo e legível
- Type-safe
- Compilador garante cobertura de casos
