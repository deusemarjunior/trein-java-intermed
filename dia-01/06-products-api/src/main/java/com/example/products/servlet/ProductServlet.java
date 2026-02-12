package com.example.products.servlet;

import com.example.products.dao.ProductDAO;
import com.example.products.dto.CreateProductRequest;
import com.example.products.dto.ProductResponse;
import com.example.products.model.Product;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servlet REST para gerenciamento de produtos.
 * 
 * Mapeia os endpoints:
 *   GET    /api/products           → Lista todos os produtos
 *   GET    /api/products?category= → Filtra por categoria
 *   GET    /api/products?name=     → Busca por nome
 *   GET    /api/products/{id}      → Busca por ID
 *   POST   /api/products           → Cria novo produto
 *   PUT    /api/products/{id}      → Atualiza produto
 *   DELETE /api/products/{id}      → Deleta produto
 * 
 * Em Spring Boot, isso seria um @RestController com
 * @GetMapping, @PostMapping, etc.
 */
public class ProductServlet extends HttpServlet {

    private final ProductDAO productDAO = new ProductDAO();
    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
            .create();

    // ═══════════════════════════════════════════════════════════
    //  GET /api/products         → Lista todos
    //  GET /api/products/{id}    → Busca por ID
    //  GET /api/products?category=Electronics → Filtra
    // ═══════════════════════════════════════════════════════════
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setJsonResponse(resp);
        String pathInfo = req.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            // Verificar filtros por query parameter
            String category = req.getParameter("category");
            String name = req.getParameter("name");

            List<Product> products;
            if (category != null && !category.isBlank()) {
                products = productDAO.findByCategory(category);
            } else if (name != null && !name.isBlank()) {
                products = productDAO.findByNameContaining(name);
            } else {
                products = productDAO.findAll();
            }

            List<ProductResponse> response = products.stream()
                    .map(ProductResponse::from)
                    .toList();

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(gson.toJson(response));
        } else {
            // Buscar por ID
            Long id = extractId(pathInfo);
            if (id == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                writeJson(resp, new ErrorResponse("Invalid product ID"));
                return;
            }

            productDAO.findById(id).ifPresentOrElse(
                    product -> {
                        resp.setStatus(HttpServletResponse.SC_OK);
                        writeJson(resp, ProductResponse.from(product));
                    },
                    () -> {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        writeJson(resp, new ErrorResponse("Product not found: " + id));
                    }
            );
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  POST /api/products  → Criar produto
    // ═══════════════════════════════════════════════════════════
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setJsonResponse(resp);

        try {
            String body = new String(req.getInputStream().readAllBytes());
            CreateProductRequest request = gson.fromJson(body, CreateProductRequest.class);

            Product product = new Product(
                    request.name(),
                    request.description(),
                    request.price(),
                    request.category()
            );

            Product saved = productDAO.save(product);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            writeJson(resp, ProductResponse.from(saved));

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, new ErrorResponse(e.getMessage()));
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, new ErrorResponse("Invalid JSON format"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  PUT /api/products/{id}  → Atualizar produto
    // ═══════════════════════════════════════════════════════════
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setJsonResponse(resp);
        Long id = extractId(req.getPathInfo());

        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, new ErrorResponse("Product ID is required in path"));
            return;
        }

        try {
            String body = new String(req.getInputStream().readAllBytes());
            CreateProductRequest request = gson.fromJson(body, CreateProductRequest.class);

            Product product = new Product(
                    request.name(),
                    request.description(),
                    request.price(),
                    request.category()
            );

            productDAO.update(id, product).ifPresentOrElse(
                    updated -> {
                        resp.setStatus(HttpServletResponse.SC_OK);
                        writeJson(resp, ProductResponse.from(updated));
                    },
                    () -> {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        writeJson(resp, new ErrorResponse("Product not found: " + id));
                    }
            );

        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, new ErrorResponse(e.getMessage()));
        } catch (JsonSyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, new ErrorResponse("Invalid JSON format"));
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  DELETE /api/products/{id}  → Deletar produto
    // ═══════════════════════════════════════════════════════════
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        setJsonResponse(resp);
        Long id = extractId(req.getPathInfo());

        if (id == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            writeJson(resp, new ErrorResponse("Product ID is required in path"));
            return;
        }

        if (productDAO.deleteById(id)) {
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } else {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            writeJson(resp, new ErrorResponse("Product not found: " + id));
        }
    }

    // ═══════════════════════════════════════════════════════════
    //  HELPERS
    // ═══════════════════════════════════════════════════════════

    private void setJsonResponse(HttpServletResponse resp) {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
    }

    private Long extractId(String pathInfo) {
        if (pathInfo == null || pathInfo.equals("/")) {
            return null;
        }
        try {
            return Long.parseLong(pathInfo.substring(1));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private void writeJson(HttpServletResponse resp, Object obj) {
        try {
            resp.getWriter().write(gson.toJson(obj));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    // DTO interno para respostas de erro
    record ErrorResponse(String error) {}
}
