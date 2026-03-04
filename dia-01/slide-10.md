# Slide 10: Configurando o Projeto Servlet + JDBC

**Horário:** 13:30 - 13:50

---

## 🎬 DEMO: Criando o projeto products-api

### Estrutura do Projeto

```
06-products-api/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/example/products/
│       │       ├── ProductsApp.java           # Main (Tomcat embedded)
│       │       ├── servlet/
│       │       │   ├── ProductServlet.java     # REST endpoints
│       │       │   └── LocalDateTimeAdapter.java # Gson adapter
│       │       ├── dao/
│       │       │   └── ProductDAO.java         # Acesso a dados (JDBC)
│       │       ├── model/
│       │       │   └── Product.java            # Modelo de dados
│       │       ├── dto/
│       │       │   ├── CreateProductRequest.java
│       │       │   └── ProductResponse.java
│       │       └── config/
│       │           └── DatabaseConfig.java     # Configuração do banco (DDL inline)
├── pom.xml
└── README.md
```

---

## Maven (pom.xml)

```xml
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.example</groupId>
    <artifactId>products-api</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>

    <properties>
        <java.version>21</java.version>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    </properties>

    <dependencies>
        <!-- Tomcat Embedded (servidor web) -->
        <dependency>
            <groupId>org.apache.tomcat.embed</groupId>
            <artifactId>tomcat-embed-core</artifactId>
            <version>10.1.18</version>
        </dependency>

        <!-- Servlet API (Jakarta) -->
        <dependency>
            <groupId>jakarta.servlet</groupId>
            <artifactId>jakarta.servlet-api</artifactId>
            <version>6.0.0</version>
            <scope>provided</scope>
        </dependency>

        <!-- H2 Database -->
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <version>2.2.224</version>
        </dependency>

        <!-- Gson (JSON) -->
        <dependency>
            <groupId>com.google.code.gson</groupId>
            <artifactId>gson</artifactId>
            <version>2.10.1</version>
        </dependency>
    </dependencies>
</project>
```

**Observe:**
- ❌ SEM Spring Boot
- ❌ SEM JPA/Hibernate
- ❌ SEM Lombok
- ✅ Tomcat embedded (mesma ideia do Spring Boot)
- ✅ JDBC puro (H2 database)
- ✅ Gson para JSON (sem Jackson)

---

## Classe Principal (Main)

```java
package com.example.products;

import com.example.products.config.DatabaseConfig;
import com.example.products.servlet.ProductServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

public class ProductsApp {

    public static void main(String[] args) throws Exception {
        // 1. Inicializar banco de dados
        DatabaseConfig.initialize();
        System.out.println("✅ Database initialized");

        // 2. Configurar Tomcat embedded
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);
        tomcat.getConnector(); // Ativa o conector HTTP

        // 3. Criar contexto da aplicação
        Context ctx = tomcat.addContext("", null);

        // 4. Registrar Servlets
        Tomcat.addServlet(ctx, "ProductServlet", new ProductServlet());
        ctx.addServletMappingDecoded("/api/products/*", "ProductServlet");

        // 5. Iniciar servidor
        tomcat.start();
        System.out.println("🚀 Server started on http://localhost:8080");
        System.out.println("📡 Products API: http://localhost:8080/api/products");
        tomcat.getServer().await();
    }
}
```

**Compare com Spring Boot:**
```java
// Spring Boot (mágica)
@SpringBootApplication
public class ProductsApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ProductsApiApplication.class, args);
    }
}
// → Auto-configura Tomcat, Servlets, DataSource, JPA... tudo!
```

---

## Configuração do Banco (JDBC)

```java
package com.example.products.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseConfig {

    private static final String URL = "jdbc:h2:mem:productsdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    public static void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS products (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(100) NOT NULL,
                    description VARCHAR(500),
                    price DECIMAL(10,2) NOT NULL,
                    category VARCHAR(50),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            
            System.out.println("✅ Table 'products' created");
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}
```

