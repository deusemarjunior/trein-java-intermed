# Slide 13: Testando a API Servlet

**Horário:** 15:00 - 15:30

---

## 🎬 DEMO: Testando com Postman/cURL

### 1️⃣ Iniciar aplicação
```bash
cd 06-products-api
mvn clean compile exec:java -Dexec.mainClass="com.example.products.ProductsApp"
```

**Output esperado:**
```
✅ Table 'products' created
✅ Database initialized
🚀 Server started on http://localhost:8080
📡 Products API: http://localhost:8080/api/products
```

---

### 2️⃣ Criar produto (POST)
```http
POST http://localhost:8080/api/products
Content-Type: application/json

{
  "name": "Laptop Gaming",
  "description": "High-end gaming laptop with RTX 4080",
  "price": 7500.00,
  "category": "Electronics"
}
```

**Response (201 Created):**
```json
{
  "id": 1,
  "name": "Laptop Gaming",
  "description": "High-end gaming laptop with RTX 4080",
  "price": 7500.00,
  "category": "Electronics",
  "createdAt": "2026-02-12T14:35:22.123"
}
```

---

### 3️⃣ Listar todos (GET)
```http
GET http://localhost:8080/api/products
```

**Response (200 OK):**
```json
[
  {
    "id": 1,
    "name": "Laptop Gaming",
    "description": "High-end gaming laptop with RTX 4080",
    "price": 7500.00,
    "category": "Electronics",
    "createdAt": "2026-02-12T14:35:22.123"
  }
]
```

---

### 4️⃣ Buscar por ID (GET)
```http
GET http://localhost:8080/api/products/1
```

---

### 5️⃣ Atualizar (PUT)
```http
PUT http://localhost:8080/api/products/1
Content-Type: application/json

{
  "name": "Laptop Gaming Pro",
  "description": "Ultimate gaming laptop with RTX 4090",
  "price": 9500.00,
  "category": "Electronics"
}
```

---

### 6️⃣ Deletar (DELETE)
```http
DELETE http://localhost:8080/api/products/1
```

**Response (204 No Content)**

---

## 🐞 Troubleshooting

### Erro: "Port 8080 already in use"
```bash
# Windows
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Linux/Mac
lsof -ti:8080 | xargs kill -9
```

### Erro: "Invalid JSON"
- Verifique aspas duplas no JSON
- Content-Type deve ser `application/json`

### Erro: "NullPointerException no DAO"
- Verifique se `DatabaseConfig.initialize()` foi chamado antes
- Confira a URL JDBC

---

## 📊 Exercício Rápido (15 min)

### Adicione busca por categoria!

```java
// No ProductDAO, adicione:
public List<Product> findByCategory(String category) {
    String sql = "SELECT * FROM products WHERE category = ? ORDER BY id";
    // ... implemente usando PreparedStatement
}

// No ProductServlet, no doGet():
String category = req.getParameter("category");
if (category != null) {
    // filtrar por categoria
}
```

**Teste:**
```http
GET http://localhost:8080/api/products?category=Electronics
```

