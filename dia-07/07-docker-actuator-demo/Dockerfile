# ── Stage 1: Build ──────────────────────────────────────────
FROM docker.io/library/eclipse-temurin:21-jdk-alpine AS build

WORKDIR /app

# Copiar arquivos de dependência primeiro (cache de camadas)
COPY pom.xml .
COPY .mvn .mvn
COPY mvnw .
RUN chmod +x mvnw

# Baixar dependências (camada cacheada)
RUN ./mvnw dependency:resolve -q

# Copiar código-fonte
COPY src ./src

# Build do projeto (sem testes)
RUN ./mvnw clean package -DskipTests -q

# ── Stage 2: Runtime ───────────────────────────────────────
FROM docker.io/library/eclipse-temurin:21-jre-alpine

WORKDIR /app

# Criar usuário não-root
RUN addgroup -S spring && adduser -S spring -G spring

# Copiar JAR do stage de build
COPY --from=build /app/target/*.jar app.jar

# Trocar para usuário não-root
USER spring:spring

# Porta da aplicação
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD wget --quiet --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Executar aplicação
ENTRYPOINT ["java", "-jar", "app.jar"]
