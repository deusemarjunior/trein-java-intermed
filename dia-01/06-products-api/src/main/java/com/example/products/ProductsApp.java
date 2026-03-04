package com.example.products;

import com.example.products.config.DatabaseConfig;
import com.example.products.servlet.ProductServlet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;

/**
 * Classe principal da aplicação.
 * 
 * Usa Tomcat Embedded para servir a API REST.
 * NÃO usa Spring Boot - tudo é configurado manualmente.
 */
public class ProductsApp {

    public static void main(String[] args) throws Exception {
        // 1. Inicializar banco de dados (criar tabelas)
        DatabaseConfig.initialize();
        System.out.println("✅ Database initialized");

        // 2. Configurar Tomcat embedded
        Tomcat tomcat = new Tomcat();
        int port = Integer.parseInt(System.getProperty("server.port", "8080"));
        tomcat.setPort(port);
        tomcat.getConnector(); // Ativa o conector HTTP

        // 3. Criar contexto da aplicação
        String docBase = new File(".").getAbsolutePath();
        Context ctx = tomcat.addContext("", docBase);

        // 4. Registrar Servlets manualmente
        Tomcat.addServlet(ctx, "ProductServlet", new ProductServlet());
        ctx.addServletMappingDecoded("/api/products/*", "ProductServlet");

        // 5. Iniciar servidor
        tomcat.start();
        System.out.println("🚀 Server started on http://localhost:" + port);
        System.out.println("📡 Products API: http://localhost:" + port + "/api/products");
        System.out.println();
        System.out.println("Endpoints disponíveis:");
        System.out.println("  GET    /api/products          - Listar todos");
        System.out.println("  GET    /api/products/{id}     - Buscar por ID");
        System.out.println("  GET    /api/products?category=X - Buscar por categoria");
        System.out.println("  POST   /api/products          - Criar produto");
        System.out.println("  PUT    /api/products/{id}     - Atualizar produto");
        System.out.println("  DELETE /api/products/{id}     - Deletar produto");

        tomcat.getServer().await();
    }
}
