# ── TODO 1: Transforme este Dockerfile em multi-stage build ──
#
# Atualmente este Dockerfile usa uma imagem JDK completa (~800MB).
# Transforme em multi-stage build para reduzir para ~200MB:
#
# Stage 1 (build):
#   FROM eclipse-temurin:21-jdk-alpine AS build
#   WORKDIR /app
#   COPY pom.xml .
#   COPY .mvn .mvn
#   COPY mvnw .
#   RUN chmod +x mvnw
#   RUN ./mvnw dependency:resolve -q
#   COPY src ./src
#   RUN ./mvnw clean package -DskipTests -q
#
# Stage 2 (runtime):
#   FROM eclipse-temurin:21-jre-alpine
#   WORKDIR /app
#   RUN addgroup -S spring && adduser -S spring -G spring
#   COPY --from=build /app/target/*.jar app.jar
#   USER spring:spring
#   EXPOSE 8092
#   HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
#     CMD wget --quiet --tries=1 --spider http://localhost:8092/actuator/health || exit 1
#   ENTRYPOINT ["java", "-jar", "app.jar"]

# ❌ Versão atual (NÃO otimizada — ~800MB)
FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY . .

RUN chmod +x mvnw && ./mvnw clean package -DskipTests -q

EXPOSE 8092

ENTRYPOINT ["java", "-jar", "target/employee-api-production-0.0.1-SNAPSHOT.jar"]
