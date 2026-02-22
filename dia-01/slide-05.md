# Slide 5: Text Blocks

**Horário:** 10:10 - 10:25

---

## Antes era assim... 😢

### Opção 1: Concatenação com +

```java
String json = "{\n" +
              "  \"name\": \"Laptop\",\n" +
              "  \"price\": 3500,\n" +
              "  \"inStock\": true\n" +
              "}";

String sql = "SELECT p.id, p.name, p.price \n" +
             "FROM products p \n" +
             "WHERE p.category = 'electronics' \n" +
             "  AND p.price > 1000 \n" +
             "ORDER BY p.price DESC";

String html = "<html>\n" +
              "  <body>\n" +
              "    <h1>Welcome</h1>\n" +
              "  </body>\n" +
              "</html>";
```

### Opção 2: StringBuilder (menos legível)

```java
StringBuilder json = new StringBuilder();
json.append("{\n");
json.append("  \"name\": \"Laptop\",\n");
json.append("  \"price\": 3500,\n");
json.append("  \"inStock\": true\n");
json.append("}");
String jsonString = json.toString();

StringBuilder sql = new StringBuilder();
sql.append("SELECT p.id, p.name, p.price \n");
sql.append("FROM products p \n");
sql.append("WHERE p.category = 'electronics' \n");
sql.append("  AND p.price > 1000 \n");
sql.append("ORDER BY p.price DESC");
String sqlString = sql.toString();

// Muito verboso e difícil de ler! 😫
```

---

## ✨ Agora com Text Blocks (Java 15+)

```java
String json = """
    {
      "name": "Laptop",
      "price": 3500,
      "inStock": true
    }
    """;

String sql = """
    SELECT p.id, p.name, p.price
    FROM products p
    WHERE p.category = 'electronics'
      AND p.price > 1000
    ORDER BY p.price DESC
    """;

String html = """
    <html>
      <body>
        <h1>Welcome</h1>
      </body>
    </html>
    """;
```

---

## 🎬 DEMO: Interpolação e Formatação

```java
public class TextBlocksDemo {
    
    public static void main(String[] args) {
        String name = "Laptop Gaming";
        BigDecimal price = BigDecimal.valueOf(4500);
        
        // ✅ Com formatação
        String json = """
            {
              "product": "%s",
              "price": %.2f,
              "currency": "BRL"
            }
            """.formatted(name, price);
        
        System.out.println(json);
        
        // ✅ SQL com parâmetros
        Long categoryId = 5L;
        String sql = """
            SELECT p.*
            FROM products p
            WHERE p.category_id = %d
              AND p.active = true
            """.formatted(categoryId);
        
        System.out.println(sql);
    }
}
```

---

## ⚠️ Cuidado

- Identação importa! É preservada
- Útil para testes, queries, JSON, XML, HTML
- Não substitui templates complexos (use Thymeleaf, etc)
