# Text Blocks Demo - Java 15+

Demonstração de **Text Blocks** introduzidos no Java 15.

## 📝 O que são Text Blocks?

Text blocks são strings multilinha delimitadas por `"""` que:
- Eliminam necessidade de escapes (`\n`, `\"`)
- Preservam formatação e indentação
- Tornam código mais legível

## 🚀 Como executar

```bash
# Compilar
mvn clean compile

# Executar
mvn exec:java -Dexec.mainClass="com.example.textblocks.TextBlocksDemo"
```

Ou usando Java diretamente:
```bash
javac -d target/classes src/main/java/com/example/textblocks/*.java
java -cp target/classes com.example.textblocks.TextBlocksDemo
```

## 🎯 Conceitos Demonstrados

1. **JSON** - Comparação antes/depois
2. **SQL** - Queries multilinha
3. **HTML** - Templates
4. **Formatação** - Uso com `.formatted()`
5. **Caso Prático** - Template de email

## 💡 Sintaxe

```java
// Básico
String text = """
    Linha 1
    Linha 2
    """;

// Com formatação
String formatted = """
    Nome: %s
    Idade: %d
    """.formatted("João", 25);
```

## ✅ Quando usar?

**USE para:**
- JSON, XML, YAML
- SQL queries
- HTML/templates
- Mensagens de erro multilinha
- Documentação em código

**Evite:**
- Strings simples de uma linha
- Quando precisa manipulação complexa
