# 03-bad-practices-lab

## 🎯 Objetivo

Este projeto contém **código funcional com más práticas propositais**.  
Sua missão é **refatorar** o código aplicando Clean Code, DTOs, camadas e tratamento de erros — sem quebrar os testes.

## 🚫 Más Práticas Encontradas

| # | Problema | Onde |
|---|----------|------|
| 1 | **God Method** — método `createOrder()` > 100 linhas | `OrderController` |
| 2 | **Nomes sem significado** — `x`, `temp`, `d`, `res` | `OrderService` |
| 3 | **Números mágicos** — `0.1`, `0.15`, `1412.0` | `OrderService` |
| 4 | **Código duplicado** — cálculo de desconto repetido | `OrderService` |
| 5 | **Entity exposta na API** — sem DTOs | `OrderController` |
| 6 | **try/catch genérico** — `catch (Exception e)` | `OrderService` |
| 7 | **Cadeia de if/else** — cálculo de frete | `OrderService` |
| 8 | **Sem tratamento global** — erros 500 genéricos | `OrderController` |
| 9 | **Sem validação** — aceita dados inválidos | `OrderController` |

## ✅ Regra de Ouro

> **Os testes devem continuar passando antes E depois da refatoração.**

## 🧪 Executar Testes

```bash
./mvnw test
```

## 🚀 Executar a Aplicação

```bash
./mvnw spring-boot:run
```

- Porta: **8085**
- H2 Console: http://localhost:8085/h2-console
- JDBC URL: `jdbc:h2:mem:ordersdb`

## 📋 Roteiro de Refatoração

1. Execute os testes e confirme que passam ✅
2. Identifique os 9 problemas listados acima
3. Para cada problema, aplique a técnica de refatoração adequada:
   - **God Method** → Extract Method
   - **Nomes ruins** → Rename Variable/Method
   - **Números mágicos** → Extract Constant
   - **Código duplicado** → Extract Method + DRY
   - **Entity exposta** → Criar DTOs + Mapper
   - **try/catch genérico** → Exceções específicas
   - **if/else chain** → Strategy ou Map
   - **Sem tratamento global** → @ControllerAdvice
   - **Sem validação** → Bean Validation + @Valid
4. Após cada refatoração, execute os testes novamente ✅
5. Ao final, todos os testes devem continuar passando ✅

## 💡 Dicas

- Use o atalho **Ctrl+Shift+R** (IntelliJ) ou **Ctrl+Shift+P > Refactor** (VS Code)
- O IntelliJ e VS Code possuem ações de refatoração automatizadas
- Faça commits após cada refatoração bem-sucedida
