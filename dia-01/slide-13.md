# Slide 13: Testando a API

**Horário:** 14:30 - 15:00

---

## 🎬 DEMO: Testando com Postman

### 1️⃣ Iniciar aplicação
```bash
./mvnw spring-boot:run
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
  "createdAt": "2026-02-03T14:35:22.123"
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
    "createdAt": "2026-02-03T14:35:22.123"
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

## 🐞 Debugging - Ver logs no console

```bash
# SQL executado
Hibernate: insert into products (category,created_at,description,name,price,updated_at) 
values (?,?,?,?,?,?)

# Request recebido
2026-02-03 14:35:22.456 DEBUG --- [nio-8080-exec-1] c.e.p.controller.ProductController 
: Creating product: Laptop Gaming
```

---

## 🔍 H2 Console - Ver banco de dados

1. Acessar: http://localhost:8080/h2-console
2. JDBC URL: `jdbc:h2:mem:testdb`
3. User: `sa`
4. Password: (deixar vazio)

```sql
-- Ver todos os produtos
SELECT * FROM PRODUCTS;

-- Inserir via SQL
INSERT INTO PRODUCTS (NAME, DESCRIPTION, PRICE, CATEGORY, CREATED_AT, UPDATED_AT)
VALUES ('Mouse', 'Gaming mouse', 250.00, 'Electronics', NOW(), NOW());
```
