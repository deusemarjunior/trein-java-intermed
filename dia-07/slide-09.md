# Slide 9: CI/CD — Conceitos

**Horário:** 13:15 - 13:30

---

## O que é CI/CD?

```mermaid
graph LR
    subgraph "CI — Integração Contínua"
        PUSH["git push"] --> BUILD["Build<br/>Automático"]
        BUILD --> TEST["Testes<br/>Unitários"]
        TEST --> ANALYSIS["Análise<br/>Estática"]
        ANALYSIS --> ARTIFACT["Artifact<br/>(JAR/Image)"]
    end

    subgraph "CD — Entrega Contínua"
        ARTIFACT --> STAGING["Deploy<br/>Staging"]
        STAGING --> APPROVAL["Aprovação<br/>(manual ou auto)"]
        APPROVAL --> PROD["Deploy<br/>Produção"]
    end

    style PUSH fill:#3498db,color:#fff
    style ARTIFACT fill:#2ecc71,color:#fff
    style PROD fill:#9b59b6,color:#fff
```

---

## Integração Contínua (CI)

**O que é**: A cada `git push`, o código é automaticamente compilado e testado. Se algo quebra, a equipe é notificada imediatamente.

```mermaid
sequenceDiagram
    participant DEV as Desenvolvedor
    participant GIT as GitHub
    participant CI as GitHub Actions
    participant TEAM as Equipe

    DEV->>GIT: git push origin feat/new-endpoint
    GIT->>CI: Trigger: push event

    CI->>CI: 1. Checkout code
    CI->>CI: 2. Setup JDK 21
    CI->>CI: 3. mvn compile
    CI->>CI: 4. mvn test (unitários)
    CI->>CI: 5. mvn verify (integração)
    CI->>CI: 6. Análise estática (SonarQube)

    alt Tudo passou ✅
        CI-->>GIT: ✅ Status: Success
        CI->>CI: 7. podman build → push image
    else Algo falhou ❌
        CI-->>GIT: ❌ Status: Failed
        CI-->>TEAM: 🔔 Notificação: "Build failed"
        Note over TEAM: Corrigir ANTES de fazer merge!
    end
```

> **Regra de ouro**: "Código que não passa no pipeline **não vai para produção**."

---

## Entrega Contínua vs. Deploy Contínuo

```mermaid
graph TD
    subgraph "Entrega Contínua (Continuous Delivery)"
        CD1["CI passou ✅"] --> CD2["Deploy automático<br/>para Staging"]
        CD2 --> CD3["🔘 Aprovação MANUAL<br/>para Produção"]
        CD3 --> CD4["Deploy Produção"]
    end

    subgraph "Deploy Contínuo (Continuous Deployment)"
        CDE1["CI passou ✅"] --> CDE2["Deploy automático<br/>para Staging"]
        CDE2 --> CDE3["Testes automáticos<br/>em Staging"]
        CDE3 --> CDE4["Deploy automático<br/>para Produção ⚡"]
    end

    style CD3 fill:#f39c12,color:#fff
    style CDE4 fill:#2ecc71,color:#fff
```

| Conceito | Descrição | Aprovação |
|----------|-----------|-----------|
| **Entrega Contínua** | Deploy para staging é automático, para produção é manual | Manual |
| **Deploy Contínuo** | Tudo automático, incluindo produção | Automática |

> A maioria das empresas usa **Entrega Contínua** (com aprovação manual antes de produção).

---

## Pipeline Típico — Etapas

```mermaid
graph LR
    E1["📥 Checkout<br/>Baixa o código"] --> E2["🔨 Build<br/>mvn compile"]
    E2 --> E3["🧪 Testes Unitários<br/>mvn test"]
    E3 --> E4["🔬 Testes Integração<br/>Testcontainers"]
    E4 --> E5["📊 Análise Estática<br/>SonarQube"]
    E5 --> E6["🐳 Podman Build<br/>podman build -t app"]
    E6 --> E7["📦 Push Image<br/>Podman Registry"]
    E7 --> E8["🚀 Deploy<br/>Staging → Prod"]

    style E1 fill:#3498db,color:#fff
    style E3 fill:#2ecc71,color:#fff
    style E4 fill:#2ecc71,color:#fff
    style E5 fill:#9b59b6,color:#fff
    style E6 fill:#f39c12,color:#fff
    style E8 fill:#e74c3c,color:#fff
```

---

## Ferramentas de CI/CD

| Ferramenta | Tipo | Destaque |
|------------|------|----------|
| **GitHub Actions** | Cloud (GitHub) | Integrado com GitHub, YAML simples |
| **GitLab CI** | Cloud (GitLab) | Pipeline como código, runners próprios |
| **Jenkins** | Self-hosted | Mais flexível, mais complexo |
| **Azure DevOps** | Cloud (Microsoft) | Integrado com Azure, boards + repos + pipelines |
| **CircleCI** | Cloud | Rápido, orbs pré-configurados |

### Exemplo: GitHub Actions (`.github/workflows/ci.yml`)

```yaml
name: CI Pipeline

on:
  push:
    branches: [main, develop]
  pull_request:
    branches: [main]

jobs:
  build:
    runs-on: ubuntu-latest

    services:
      postgres:
        image: postgres:16-alpine
        env:
          POSTGRES_DB: testdb
          POSTGRES_USER: test
          POSTGRES_PASSWORD: test
        ports:
          - 5432:5432
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5

    steps:
      - uses: actions/checkout@v4

      - name: Setup JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'

      - name: Cache Maven packages
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-maven-${{ hashFiles('**/pom.xml') }}

      - name: Build & Test
        run: mvn clean verify
        env:
          SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/testdb

      - name: Build Podman Image
        run: podman build -t my-app:${{ github.sha }} .
```

---

## Por que CI/CD é Obrigatório

```mermaid
graph TD
    subgraph "Sem CI/CD"
        S1["Dev faz build local"] --> S2["'Funcionou na minha máquina'"]
        S2 --> S3["Deploy manual via FTP/SSH"]
        S3 --> S4["🔴 Bug em produção<br/>'Quem deployou?'"]
    end

    subgraph "Com CI/CD"
        C1["Dev faz push"] --> C2["Pipeline roda automaticamente"]
        C2 --> C3["Testes passam? Deploy automático"]
        C3 --> C4["✅ Rastreável, reproduzível,<br/>seguro"]
    end

    style S4 fill:#e74c3c,color:#fff
    style C4 fill:#2ecc71,color:#fff
```

---

## 🎯 Quiz Rápido

1. **Qual a diferença entre CI e CD?**
   - CI: build e teste automáticos a cada push. CD: deploy automático ou semi-automático.

2. **Se os testes falham no pipeline, o que acontece?**
   - O merge/deploy é **bloqueado**. O dev precisa corrigir antes.

3. **Por que cachear dependências Maven no CI?**
   - Para não baixar todas as dependências a cada build (~2min economizados).
