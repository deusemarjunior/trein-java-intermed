# Slide 6: Liskov Substitution Principle (LSP)

---

## 📖 Definição

> **"Objetos de uma superclasse devem poder ser substituídos por objetos de suas subclasses sem quebrar a aplicação"**
> 
> *— Barbara Liskov*

```mermaid
graph TD
    A[Cliente usa<br/>SuperClasse] --> B{Substitui por<br/>SubClasse}
    B -->|✅ LSP| C[Funciona<br/>corretamente]
    B -->|❌ Quebra LSP| D[Erro ou<br/>comportamento inesperado]
    
    style C fill:#4CAF50,stroke:#2E7D32,color:#fff
    style D fill:#f44336,stroke:#c62828,color:#fff
```

---

## ❌ Violação Clássica: Quadrado e Retângulo

```java
public class Rectangle {
    protected int width;
    protected int height;
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
}

// ❌ Violação: Square é um Rectangle, mas quebra o contrato!
public class Square extends Rectangle {
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;  // ⚠️ Modifica altura também!
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height;   // ⚠️ Modifica largura também!
        this.height = height;
    }
}
```

---

## 💥 O Problema

```java
public class AreaCalculator {
    public void testRectangle(Rectangle rect) {
        rect.setWidth(5);
        rect.setHeight(4);
        
        // Esperado: 20
        assert rect.getArea() == 20;  // ✅ Funciona com Rectangle
                                       // ❌ FALHA com Square (área = 16)
    }
}
```

```mermaid
sequenceDiagram
    participant Client
    participant Rectangle
    participant Square
    
    Client->>Rectangle: setWidth(5)
    Rectangle-->>Client: width=5, height=?
    Client->>Rectangle: setHeight(4)
    Rectangle-->>Client: width=5, height=4
    Client->>Rectangle: getArea()
    Rectangle-->>Client: 20 ✅
    
    Client->>Square: setWidth(5)
    Square-->>Client: width=5, height=5 ⚠️
    Client->>Square: setHeight(4)
    Square-->>Client: width=4, height=4 ⚠️
    Client->>Square: getArea()
    Square-->>Client: 16 ❌
```

---

## ✅ Solução: Não use herança incorretamente!

```mermaid
classDiagram
    class Shape {
        <<interface>>
        +getArea() int
    }
    
    class Rectangle {
        -width: int
        -height: int
        +setWidth(int)
        +setHeight(int)
        +getArea() int
    }
    
    class Square {
        -side: int
        +setSide(int)
        +getArea() int
    }
    
    Shape <|.. Rectangle
    Shape <|.. Square
    
    style Shape fill:#4CAF50,stroke:#2E7D32,color:#fff
```

```java
public interface Shape {
    int getArea();
}

public class Rectangle implements Shape {
    private int width;
    private int height;
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
}

public class Square implements Shape {
    private int side;
    
    public void setSide(int side) {
        this.side = side;
    }
    
    public int getArea() {
        return side * side;
    }
}
```

---

## ❌ Violação em Spring Boot

```java
public class Bird {
    public void fly() {
        System.out.println("Flying...");
    }
}

// ❌ Pinguim não voa, mas herda fly()!
public class Penguin extends Bird {
    @Override
    public void fly() {
        throw new UnsupportedOperationException("Penguins can't fly!");
    }
}

// Cliente espera que Bird possa voar
public class BirdService {
    public void makeBirdFly(Bird bird) {
        bird.fly();  // 💥 Quebra com Penguin!
    }
}
```

---

## ✅ Solução com Interface Segregation

```mermaid
classDiagram
    class Bird {
        <<abstract>>
        +eat()
        +sleep()
    }
    
    class Flyable {
        <<interface>>
        +fly()
    }
    
    class Swimmable {
        <<interface>>
        +swim()
    }
    
    class Eagle {
        +eat()
        +sleep()
        +fly()
    }
    
    class Penguin {
        +eat()
        +sleep()
        +swim()
    }
    
    class Duck {
        +eat()
        +sleep()
        +fly()
        +swim()
    }
    
    Bird <|-- Eagle
    Bird <|-- Penguin
    Bird <|-- Duck
    Flyable <|.. Eagle
    Flyable <|.. Duck
    Swimmable <|.. Penguin
    Swimmable <|.. Duck
    
    style Bird fill:#4CAF50,stroke:#2E7D32,color:#fff
    style Flyable fill:#2196F3,stroke:#1565C0,color:#fff
    style Swimmable fill:#2196F3,stroke:#1565C0,color:#fff
```

---

## 🎯 Regras para LSP

```mermaid
flowchart TD
    A[Criar Subclasse] --> B{Precondições<br/>mais fracas?}
    B -->|Não| C{Pós-condições<br/>mais fortes?}
    B -->|Sim| X[❌ Viola LSP]
    C -->|Não| D{Invariantes<br/>preservadas?}
    C -->|Sim| X
    D -->|Sim| E{Exceções<br/>compatíveis?}
    D -->|Não| X
    E -->|Sim| F[✅ Respeita LSP]
    E -->|Não| X
    
    style F fill:#4CAF50,stroke:#2E7D32,color:#fff
    style X fill:#f44336,stroke:#c62828,color:#fff
```

### Explicação:
1. **Precondições mais fracas**: Subclasse não deve exigir mais do que a superclasse
2. **Pós-condições mais fortes**: Subclasse deve garantir pelo menos o mesmo que a superclasse
3. **Invariantes**: Regras que devem ser sempre verdadeiras
4. **Exceções**: Subclasse não deve lançar exceções que a superclasse não lança

---

## 💡 Como Verificar LSP?

```java
// ✅ Teste: Substitua Base por Derived
@Test
public void testLiskovSubstitution() {
    // Usando superclasse
    PaymentProcessor processor = new CreditCardProcessor();
    processPayment(processor);  // Deve funcionar
    
    // Substituindo por subclasse
    processor = new DebitCardProcessor();
    processPayment(processor);  // Deve funcionar igualmente
}

private void processPayment(PaymentProcessor processor) {
    // Cliente não deve saber qual implementação está usando
    Payment result = processor.process(amount);
    assertNotNull(result);
    assertEquals(PaymentStatus.COMPLETED, result.getStatus());
}
```

---

## 🚨 Sinais de Violação

```mermaid
mindmap
  root((Violação LSP))
    Código
      instanceof checks
      Type casting
      Exceções inesperadas
      Métodos vazios/não implementados
    Comportamento
      Precondições mais fortes
      Pós-condições mais fracas
      Invariantes quebradas
    Design
      Herança por conveniência
      Hierarquia forçada
      Relação "É-UM" questionável
```

---

## 💡 Dica do Instrutor

```
⚠️ Lembre-se:
- Herança é sobre comportamento, não estrutura
- Prefira composição sobre herança
- "É-UM" vs "Comporta-se como UM"
- Se precisa de instanceof, provavelmente violou LSP
- Teste: substitua e veja se funciona sem mudanças no cliente
```
