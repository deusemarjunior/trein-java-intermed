# Dia 8 - CI/CD e Containers

**Duração**: 5 horas  
**Objetivo**: Dominar Git workflows, containerização com Docker e pipelines CI/CD

## 📋 Conteúdo Programático

### Manhã (3 horas)

#### 1. Git Avançado e Workflows (1h)

**Git Flow para times**
```bash
# Feature branch
git checkout -b feature/add-payment-api
# ... desenvolve ...
git add .
git commit -m "feat: add payment API endpoints"
git push origin feature/add-payment-api

# Pull Request → Code Review → Merge

# Hotfix
git checkout -b hotfix/fix-payment-bug main
git commit -m "fix: resolve payment gateway timeout"
git push origin hotfix/fix-payment-bug
```

**Conventional Commits**
```
feat: add user authentication
fix: resolve null pointer in order service
docs: update API documentation
style: format code according to style guide
refactor: restructure payment module
test: add integration tests for products
chore: update dependencies
perf: improve database query performance
ci: configure GitHub Actions pipeline
```

**Code Review Best Practices**
```markdown
# Pull Request Template

## Descrição
Adiciona autenticação JWT para proteger endpoints da API

## Tipo de mudança
- [ ] Bug fix
- [x] Nova feature
- [ ] Breaking change
- [ ] Documentação

## Checklist
- [x] Código segue style guide do projeto
- [x] Testes unitários adicionados/atualizados
- [x] Testes de integração adicionados
- [x] Documentação atualizada
- [x] Sem breaking changes
- [x] Build passa localmente

## Como testar
1. Executar `mvn clean install`
2. Fazer login em `/api/auth/login`
3. Usar token em endpoints protegidos
```

**Git hooks**
```bash
# .git/hooks/pre-commit
#!/bin/sh
echo "Running pre-commit hooks..."

# Formatar código
mvn spotless:apply

# Executar testes
mvn test

if [ $? -ne 0 ]; then
    echo "Tests failed. Commit aborted."
    exit 1
fi
```

#### 2. Docker - Containerização (1.5h)

**Dockerfile para Java Spring Boot**
```dockerfile
# Multi-stage build

# Stage 1: Build
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copiar apenas pom.xml primeiro (cache de dependências)
COPY pom.xml .
RUN mvn dependency:go-offline

# Copiar código fonte
COPY src ./src

# Build da aplicação
RUN mvn clean package -DskipTests

# Stage 2: Runtime
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Criar usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

# Copiar JAR do stage de build
COPY --from=build /app/target/*.jar app.jar

# Configurar health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
    CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Expor porta
EXPOSE 8080

# Configurar JVM
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# Executar aplicação
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

**Docker Compose para ambiente local**
```yaml
version: '3.8'

services:
  app:
    build:
      context: .
      dockerfile: Dockerfile
    ports:
      - "8080:8080"
    environment:
      - SPRING_PROFILES_ACTIVE=dev
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/ecommerce
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
      - SPRING_DATA_REDIS_HOST=redis
      - SPRING_DATA_REDIS_PORT=6379
    depends_on:
      postgres:
        condition: service_healthy
      redis:
        condition: service_started
    networks:
      - app-network
    healthcheck:
      test: ["CMD", "wget", "--quiet", "--tries=1", "--spider", "http://localhost:8080/actuator/health"]
      interval: 30s
      timeout: 10s
      retries: 3
      start_period: 40s

  postgres:
    image: postgres:15-alpine
    environment:
      - POSTGRES_DB=ecommerce
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data
      - ./init-scripts:/docker-entrypoint-initdb.d
    networks:
      - app-network
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U postgres"]
      interval: 10s
      timeout: 5s
      retries: 5

  redis:
    image: redis:7-alpine
    ports:
      - "6379:6379"
    volumes:
      - redis-data:/data
    networks:
      - app-network
    command: redis-server --appendonly yes

  mongodb:
    image: mongo:7
    environment:
      - MONGO_INITDB_ROOT_USERNAME=admin
      - MONGO_INITDB_ROOT_PASSWORD=admin
      - MONGO_INITDB_DATABASE=catalog
    ports:
      - "27017:27017"
    volumes:
      - mongo-data:/data/db
    networks:
      - app-network

volumes:
  postgres-data:
  redis-data:
  mongo-data:

networks:
  app-network:
    driver: bridge
```

**Docker comandos úteis**
```bash
# Build
docker build -t ecommerce-api:latest .

# Run
docker run -d -p 8080:8080 --name ecommerce-api ecommerce-api:latest

# Logs
docker logs -f ecommerce-api

# Exec
docker exec -it ecommerce-api sh

# Compose
docker-compose up -d
docker-compose down
docker-compose logs -f app

# Limpeza
docker system prune -a
docker volume prune
```

**Otimização de imagens**
```dockerfile
# Ruim: ~800MB
FROM openjdk:21
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# Melhor: ~300MB
FROM eclipse-temurin:21-jre-alpine
COPY target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]

# Ótimo: ~200MB (com multi-stage)
FROM maven:3.9-eclipse-temurin-21 AS build
# ... build ...

FROM eclipse-temurin:21-jre-alpine
COPY --from=build /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

#### 3. Azure Container Registry (30min)

**Push para ACR**
```bash
# Login no Azure
az login

# Criar registry
az acr create \
  --resource-group myResourceGroup \
  --name myregistry \
  --sku Basic

# Login no ACR
az acr login --name myregistry

# Tag da imagem
docker tag ecommerce-api:latest myregistry.azurecr.io/ecommerce-api:latest

# Push
docker push myregistry.azurecr.io/ecommerce-api:latest

# Pull
docker pull myregistry.azurecr.io/ecommerce-api:latest
```

### Tarde (2 horas)

#### 4. GitHub Actions - CI/CD (2h)

**Workflow básico**
```yaml
# .github/workflows/ci.yml
name: CI Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main, develop ]

env:
  JAVA_VERSION: '21'
  MAVEN_OPTS: -Xmx512m

jobs:
  build:
    runs-on: ubuntu-latest
    
    steps:
      - name: Checkout code
        uses: actions/checkout@v4
      
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: ${{ env.JAVA_VERSION }}
          distribution: 'temurin'
          cache: maven
      
      - name: Build with Maven
        run: mvn clean install -DskipTests
      
      - name: Run tests
        run: mvn test
      
      - name: Generate test report
        uses: dorny/test-reporter@v1
        if: always()
        with:
          name: Test Results
          path: target/surefire-reports/*.xml
          reporter: java-junit
      
      - name: Code coverage
        run: mvn jacoco:report
      
      - name: Upload coverage to Codecov
        uses: codecov/codecov-action@v3
        with:
          files: ./target/site/jacoco/jacoco.xml
          fail_ci_if_error: true
```

**Pipeline completo (Build + Deploy)**
```yaml
# .github/workflows/cd.yml
name: CD Pipeline

on:
  push:
    branches: [ main ]
    tags:
      - 'v*'

env:
  REGISTRY: myregistry.azurecr.io
  IMAGE_NAME: ecommerce-api

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 21
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
          cache: maven
      
      - name: Build and Test
        run: |
          mvn clean verify
          mvn jacoco:report
      
      - name: SonarQube Scan
        env:
          SONAR_TOKEN: ${{ secrets.SONAR_TOKEN }}
        run: |
          mvn sonar:sonar \
            -Dsonar.projectKey=ecommerce-api \
            -Dsonar.host.url=https://sonarcloud.io \
            -Dsonar.organization=myorg
      
      - name: Upload artifact
        uses: actions/upload-artifact@v3
        with:
          name: app-jar
          path: target/*.jar

  docker-build-push:
    needs: build-and-test
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Download artifact
        uses: actions/download-artifact@v3
        with:
          name: app-jar
          path: target/
      
      - name: Log in to Azure Container Registry
        uses: azure/docker-login@v1
        with:
          login-server: ${{ env.REGISTRY }}
          username: ${{ secrets.ACR_USERNAME }}
          password: ${{ secrets.ACR_PASSWORD }}
      
      - name: Extract metadata
        id: meta
        uses: docker/metadata-action@v5
        with:
          images: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}
          tags: |
            type=ref,event=branch
            type=semver,pattern={{version}}
            type=sha
      
      - name: Build and push
        uses: docker/build-push-action@v5
        with:
          context: .
          push: true
          tags: ${{ steps.meta.outputs.tags }}
          labels: ${{ steps.meta.outputs.labels }}
          cache-from: type=registry,ref=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:buildcache
          cache-to: type=registry,ref=${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:buildcache,mode=max

  deploy-to-azure:
    needs: docker-build-push
    runs-on: ubuntu-latest
    environment: production
    
    steps:
      - name: Azure Login
        uses: azure/login@v1
        with:
          creds: ${{ secrets.AZURE_CREDENTIALS }}
      
      - name: Deploy to Azure Container Apps
        uses: azure/container-apps-deploy-action@v1
        with:
          containerAppName: ecommerce-api
          resourceGroup: myResourceGroup
          imageToDeploy: ${{ env.REGISTRY }}/${{ env.IMAGE_NAME }}:${{ github.sha }}
          environmentVariables: |
            SPRING_PROFILES_ACTIVE=prod
            SPRING_DATASOURCE_URL=${{ secrets.DB_URL }}
```

**Workflow para Pull Requests**
```yaml
# .github/workflows/pr.yml
name: PR Checks

on:
  pull_request:
    types: [opened, synchronize, reopened]

jobs:
  validate:
    runs-on: ubuntu-latest
    
    steps:
      - uses: actions/checkout@v4
      
      - name: Validate PR title
        uses: amannn/action-semantic-pull-request@v5
        env:
          GITHUB_TOKEN: ${{ secrets.GITHUB_TOKEN }}
      
      - name: Set up JDK
        uses: actions/setup-java@v4
        with:
          java-version: '21'
          distribution: 'temurin'
      
      - name: Build
        run: mvn clean compile
      
      - name: Run tests
        run: mvn test
      
      - name: Check code style
        run: mvn spotless:check
      
      - name: Security scan
        uses: aquasecurity/trivy-action@master
        with:
          scan-type: 'fs'
          scan-ref: '.'
          format: 'sarif'
          output: 'trivy-results.sarif'
      
      - name: Upload security results
        uses: github/codeql-action/upload-sarif@v2
        with:
          sarif_file: 'trivy-results.sarif'
```

## 💻 Exercícios Práticos

### Exercício 1: Dockerização (1h)
- Criar Dockerfile otimizado
- Docker Compose com todos os serviços
- Scripts de build e deploy

### Exercício 2: CI/CD Pipeline (1.5h)
- Configurar GitHub Actions
- Build, test, coverage
- Deploy automatizado
- Notificações

### Exercício 3: Azure Deploy (30min)
- Push para ACR
- Deploy em Container Apps
- Configurar variáveis de ambiente

## 📚 Material de Estudo

### Leitura Obrigatória
- [Docker Best Practices](https://docs.docker.com/develop/dev-best-practices/)
- [GitHub Actions Documentation](https://docs.github.com/en/actions)
- [Azure Container Apps](https://learn.microsoft.com/azure/container-apps/)

## 🎯 Objetivos de Aprendizagem

- ✅ Dominar Git workflows
- ✅ Criar containers Docker
- ✅ Configurar pipelines CI/CD
- ✅ Deploy em Azure

## 🏠 Tarefa de Casa

1. **Configurar pipeline completo**
2. **Estudar**: Kubernetes, GitOps
3. **Preparação Dia 9**: Métricas, observabilidade

## 📝 Notas do Instrutor

```
Demonstrar:
- Git rebase vs merge
- Docker layers
- Pipeline execution
- Blue-green deployment
```

## 🔗 Links Úteis

- [Docker Hub](https://hub.docker.com/)
- [GitHub Actions Marketplace](https://github.com/marketplace?type=actions)
- [Azure Portal](https://portal.azure.com/)
