package com.example.products.dao;

import com.example.products.config.DatabaseConfig;
import com.example.products.model.Product;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data Access Object para produtos.
 * 
 * Implementa CRUD usando JDBC puro (sem JPA/Hibernate).
 * Usa PreparedStatement para segurança contra SQL Injection.
 * Usa try-with-resources para gerenciar conexões automaticamente.
 */
public class ProductDAO {

    // ═══════════════════════════════════════════════════════════
    //  CREATE
    // ═══════════════════════════════════════════════════════════

    /**
     * Salva um novo produto no banco de dados.
     * O ID é gerado automaticamente pelo banco (AUTO_INCREMENT).
     */
    public Product save(Product product) {
        String sql = """
            INSERT INTO products (name, description, price, category, created_at, updated_at)
            VALUES (?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setString(4, product.getCategory());
            ps.executeUpdate();

            // Recuperar ID gerado pelo banco
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    product.setId(rs.getLong(1));
                }
            }

            // Retornar produto completo (com timestamps do banco)
            return findById(product.getId()).orElse(product);

        } catch (SQLException e) {
            throw new RuntimeException("Error saving product", e);
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  READ
    // ═══════════════════════════════════════════════════════════

    /**
     * Lista todos os produtos ordenados por ID.
     */
    public List<Product> findAll() {
        String sql = "SELECT * FROM products ORDER BY id";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                products.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding products", e);
        }

        return products;
    }

    /**
     * Busca produto por ID. Retorna Optional.empty() se não encontrar.
     */
    public Optional<Product> findById(Long id) {
        String sql = "SELECT * FROM products WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding product by id", e);
        }

        return Optional.empty();
    }

    /**
     * Busca produtos por categoria.
     */
    public List<Product> findByCategory(String category) {
        String sql = "SELECT * FROM products WHERE LOWER(category) = LOWER(?) ORDER BY id";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, category);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by category", e);
        }

        return products;
    }

    /**
     * Busca produtos por nome (LIKE, case-insensitive).
     */
    public List<Product> findByNameContaining(String name) {
        String sql = "SELECT * FROM products WHERE LOWER(name) LIKE LOWER(?) ORDER BY id";
        List<Product> products = new ArrayList<>();

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + name + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    products.add(mapRow(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error finding products by name", e);
        }

        return products;
    }

    // ═══════════════════════════════════════════════════════════
    //  UPDATE
    // ═══════════════════════════════════════════════════════════

    /**
     * Atualiza um produto existente. Retorna o produto atualizado ou empty.
     */
    public Optional<Product> update(Long id, Product product) {
        String sql = """
            UPDATE products 
            SET name = ?, description = ?, price = ?, category = ?, 
                updated_at = CURRENT_TIMESTAMP
            WHERE id = ?
        """;

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, product.getName());
            ps.setString(2, product.getDescription());
            ps.setBigDecimal(3, product.getPrice());
            ps.setString(4, product.getCategory());
            ps.setLong(5, id);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                return findById(id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error updating product", e);
        }

        return Optional.empty();
    }

    // ═══════════════════════════════════════════════════════════
    //  DELETE
    // ═══════════════════════════════════════════════════════════

    /**
     * Deleta um produto pelo ID. Retorna true se deletou, false se não existia.
     */
    public boolean deleteById(Long id) {
        String sql = "DELETE FROM products WHERE id = ?";

        try (Connection conn = DatabaseConfig.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new RuntimeException("Error deleting product", e);
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  HELPER: Mapear ResultSet → Product
    // ═══════════════════════════════════════════════════════════

    /**
     * Converte uma linha do ResultSet em um objeto Product.
     * Este é o trabalho que o JPA/Hibernate faz automaticamente.
     */
    private Product mapRow(ResultSet rs) throws SQLException {
        Product product = new Product();
        product.setId(rs.getLong("id"));
        product.setName(rs.getString("name"));
        product.setDescription(rs.getString("description"));
        product.setPrice(rs.getBigDecimal("price"));
        product.setCategory(rs.getString("category"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            product.setCreatedAt(createdAt.toLocalDateTime());
        }

        Timestamp updatedAt = rs.getTimestamp("updated_at");
        if (updatedAt != null) {
            product.setUpdatedAt(updatedAt.toLocalDateTime());
        }

        return product;
    }
}
