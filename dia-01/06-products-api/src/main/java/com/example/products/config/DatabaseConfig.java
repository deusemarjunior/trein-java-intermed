package com.example.products.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Configuração do banco de dados H2 (em memória).
 * 
 * Gerencia conexões JDBC de forma simples.
 * Em produção, usaríamos um Connection Pool (HikariCP, etc).
 */
public class DatabaseConfig {

    private static final String URL = "jdbc:h2:mem:productsdb;DB_CLOSE_DELAY=-1";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    /**
     * Obtém uma conexão com o banco de dados.
     * IMPORTANTE: sempre feche a conexão com try-with-resources!
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /**
     * Inicializa o banco de dados criando as tabelas necessárias.
     * Deve ser chamado uma vez ao iniciar a aplicação.
     */
    public static void initialize() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {

            // Criar tabela de produtos
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

            // Inserir dados de exemplo
            stmt.execute("""
                INSERT INTO products (name, description, price, category) VALUES
                ('Laptop Gaming', 'High-end gaming laptop with RTX 4080', 7500.00, 'Electronics'),
                ('Mouse Gamer', 'Mouse com sensor de 25000 DPI', 250.00, 'Electronics'),
                ('Teclado Mecânico', 'Teclado com switches Cherry MX', 450.00, 'Electronics'),
                ('Cadeira Gamer', 'Cadeira ergonômica para longas sessões', 1800.00, 'Furniture'),
                ('Monitor 4K', 'Monitor 27 polegadas IPS 4K', 2500.00, 'Electronics')
            """);
            System.out.println("✅ Sample data inserted (5 products)");

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }
}
