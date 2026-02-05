# Slide 4: Single Responsibility Principle (SRP)

---

## 📖 Definição

> **"Uma classe deve ter apenas uma razão para mudar"**
> 
> *— Robert C. Martin*

**Ou seja:** Cada classe deve ter uma única responsabilidade bem definida

---

## ❌ Violação do SRP

```java
@Service
public class UserService {
    
    // Responsabilidade 1: Lógica de negócio
    public User createUser(String name, String email) {
        User user = new User(name, email);
        
        // Responsabilidade 2: Validação
        if (!email.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        
        // Responsabilidade 3: Persistência
        Connection conn = DriverManager.getConnection("...");
        PreparedStatement stmt = conn.prepareStatement("INSERT...");
        stmt.executeUpdate();
        
        // Responsabilidade 4: Envio de email
        MimeMessage message = new MimeMessage();
        Transport.send(message);
        
        // Responsabilidade 5: Logging
        System.out.println("User created: " + user.getId());
        
        return user;
    }
}
```

**Problema:** 5 razões diferentes para modificar esta classe! 😱

---

## ✅ Aplicando SRP

```mermaid
graph TD
    A[UserService] -->|usa| B[UserValidator]
    A -->|usa| C[UserRepository]
    A -->|usa| D[EmailService]
    A -->|usa| E[Logger]
    
    B[UserValidator<br/>Valida dados]
    C[UserRepository<br/>Persistência]
    D[EmailService<br/>Envia emails]
    E[Logger<br/>Logs]
    
    style A fill:#4CAF50,stroke:#2E7D32,color:#fff
    style B fill:#2196F3,stroke:#1565C0,color:#fff
    style C fill:#2196F3,stroke:#1565C0,color:#fff
    style D fill:#2196F3,stroke:#1565C0,color:#fff
    style E fill:#2196F3,stroke:#1565C0,color:#fff
```

---

## ✅ Código Refatorado

```java
// 1️⃣ Responsabilidade única: Validação
@Component
public class UserValidator {
    public void validate(User user) {
        if (!user.getEmail().contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
    }
}

// 2️⃣ Responsabilidade única: Persistência
@Repository
public interface UserRepository extends JpaRepository<User, Long> {}

// 3️⃣ Responsabilidade única: Envio de emails
@Service
public class EmailService {
    public void sendWelcomeEmail(User user) {
        // lógica de envio
    }
}

// 4️⃣ Responsabilidade única: Orquestração
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserValidator validator;
    private final UserRepository repository;
    private final EmailService emailService;
    
    public User createUser(String name, String email) {
        User user = new User(name, email);
        validator.validate(user);
        user = repository.save(user);
        emailService.sendWelcomeEmail(user);
        return user;
    }
}
```

---

## 📊 Comparação: Antes vs Depois

```mermaid
graph LR
    subgraph "❌ Sem SRP"
        A[UserService<br/>900 linhas<br/>5 responsabilidades]
    end
    
    subgraph "✅ Com SRP"
        B[UserService<br/>50 linhas]
        C[UserValidator<br/>100 linhas]
        D[UserRepository<br/>Interface]
        E[EmailService<br/>150 linhas]
    end
    
    style A fill:#f44336,stroke:#c62828,color:#fff
    style B fill:#4CAF50,stroke:#2E7D32,color:#fff
    style C fill:#4CAF50,stroke:#2E7D32,color:#fff
    style D fill:#4CAF50,stroke:#2E7D32,color:#fff
    style E fill:#4CAF50,stroke:#2E7D32,color:#fff
```

---

## 🎯 Benefícios

```mermaid
mindmap
  root((SRP))
    Testabilidade
      Testes unitários focados
      Mocks simples
      Alta cobertura
    Manutenibilidade
      Fácil localizar código
      Mudanças isoladas
      Menos conflitos no Git
    Reusabilidade
      Componentes independentes
      Fácil reutilizar
    Legibilidade
      Código mais claro
      Nomes descritivos
```

---

## 💡 Como Identificar Violações?

1. **Classe muito grande** (>300 linhas)
2. **Muitas importações** (>20)
3. **Nome genérico** (Manager, Helper, Util)
4. **Muitas dependências** (>5 injeções)
5. **Difícil dar um nome descritivo**

---

## 🤔 Exercício Rápido

**Identifique as responsabilidades:**

```java
@Service
public class OrderService {
    public void processOrder(Order order) {
        // Valida o pedido
        // Calcula frete
        // Aplica desconto
        // Atualiza estoque
        // Processa pagamento
        // Envia email de confirmação
        // Gera nota fiscal
        // Atualiza dashboard
    }
}
```

**Quantas responsabilidades você identificou?** 🤔
