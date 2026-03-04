# Sealed Classes Demo - Java 17+

Demonstração de **Sealed Classes** introduzidas no Java 17.

## 📝 O que são Sealed Classes?

Sealed classes permitem controlar exatamente quais classes podem estender/implementar uma classe/interface. Isso garante uma hierarquia fechada e conhecida em tempo de compilação.

## 🎯 Benefícios

- **Segurança**: Ninguém pode criar subclasses não autorizadas
- **Modelagem de domínio**: Perfeito para estados finitos (Status, Payment Types, etc)
- **Pattern Matching**: Compilador sabe todos os tipos possíveis
- **Documentação**: Hierarquia explícita no código

## 🚀 Como executar

```bash
# Compilar
mvn clean compile

# Executar
mvn exec:java -Dexec.mainClass="com.example.sealed.SealedClassesDemo"
```

Ou usando Java diretamente:
```bash
javac -d target/classes src/main/java/com/example/sealed/*.java
java -cp target/classes com.example.sealed.SealedClassesDemo
```

## 🎯 Conceitos Demonstrados

1. **Sealed Class** - `sealed class Payment permits ...`
2. **Final Classes** - Classes que não podem ser estendidas
3. **Pattern Matching** - Uso com instanceof
4. **Hierarquia Controlada** - Apenas 3 tipos de pagamento permitidos

## 💡 Estrutura

```
Payment (sealed)
├── CreditCardPayment (final)
├── PixPayment (final)
└── BoletoPayment (final)
```

## 📚 Quando usar?

✅ **USE para:**
- Tipos de pagamento
- Estados de ordem (PENDING, PROCESSING, COMPLETED)
- Tipos de usuário (ADMIN, USER, GUEST)
- Formas geométricas (Circle, Rectangle, Triangle)

❌ **NÃO USE para:**
- Hierarquias extensíveis por bibliotecas externas
- Plugins/extensões
