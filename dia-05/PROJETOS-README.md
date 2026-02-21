# 📚 Projetos Java - Dia 05

## 📖 Ordem de Estudo Recomendada

### 1️⃣ **05-integration-security-demo** (Projeto Completo - Demonstração)
**Objetivo**: Projeto completo demonstrando comunicação entre serviços com Feign Client, resiliência com Resilience4j, segurança com Spring Security + JWT e documentação com OpenAPI/Swagger.

**Conceitos**:
- `ProductClient` (Feign): consumo declarativo de API externa
- `CustomErrorDecoder`: tratamento de erros do serviço remoto (404, 500)
- Resilience4j: retry (3 tentativas, 500ms) + circuit breaker com fallback
- Configuração CORS global via `WebMvcConfigurer` para `localhost:3000`
- `AuthController`: endpoint `POST /auth/login` que retorna JWT
- `JwtAuthenticationFilter`: validação do token em cada requisição
- `SecurityFilterChain`: rotas públicas vs. protegidas por role (ADMIN/USER)
- Swagger UI em `/swagger-ui.html` com botão "Authorize" para Bearer Token

**Porta**: 8088  
**Arquivo de Testes**: `api-requests.http`

```bash
cd 05-integration-security-demo
mvn spring-boot:run      # Roda a aplicação
```

**Swagger UI**: http://localhost:8088/swagger-ui.html

---

### 2️⃣ **05-employee-api-secure** (Exercício: Integração, Segurança e Documentação)
**Objetivo**: Adicionar integração com serviço externo (Feign Client), segurança JWT e documentação Swagger à API de Funcionários.

**Conceitos**:
- Criar `DepartmentClient` (Feign) para consumir API de departamentos
- Implementar `CustomErrorDecoder` para erros do serviço remoto
- Configurar Resilience4j (Retry + Circuit Breaker + Fallback)
- Configurar CORS global para `localhost:3000`
- Implementar autenticação com Spring Security + JWT
- Documentar endpoints com SpringDoc/OpenAPI

**Porta**: 8089  
**Arquivo de Testes**: `api-requests.http`

```bash
cd 05-employee-api-secure
mvn spring-boot:run      # Roda a aplicação
```

**TODOs a implementar**: 8 (Feign Client, Error Decoder, Resilience4j, CORS, AuthController, Security, JWT Filter, Swagger)

---

## 🚀 Como Usar

### 1. **Estude primeiro o projeto completo** (05-integration-security-demo)
   - Execute a aplicação: `cd 05-integration-security-demo && mvn spring-boot:run`
   - Acesse o Swagger UI: http://localhost:8088/swagger-ui.html
   - Faça login via `POST /auth/login` e copie o JWT
   - Clique em "Authorize" no Swagger UI e cole o token
   - Teste os endpoints protegidos
   - Analise o código de cada camada (Feign, Security, Swagger)

### 2. **Pratique com o exercício** (05-employee-api-secure)
   ```bash
   cd 05-employee-api-secure
   mvn spring-boot:run   # verificar que sobe
   ```
   - Implemente os TODOs 1-8 na ordem
   - Após cada TODO, reinicie a aplicação e teste
   - Meta: Feign Client funcionando, JWT protegendo rotas, Swagger documentando tudo

---

## ⚠️ Pré-requisitos

| Requisito | Verificação |
|-----------|-------------|
| JDK 21 | `java --version` |
| Maven 3.8+ | `mvn --version` |
| Docker Desktop | `docker --version` |

> **DICA**: Use o arquivo `api-requests.http` de cada projeto para testar os endpoints diretamente no VS Code (extensão REST Client).
