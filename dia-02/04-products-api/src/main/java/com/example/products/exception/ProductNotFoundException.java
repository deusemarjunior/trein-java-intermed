package com.example.products.exception;

/**
 * Exceção customizada para produto não encontrado
 */
public class ProductNotFoundException extends RuntimeException {
    
    public ProductNotFoundException(String message) {
        super(message);
    }
}
