package com.example.cleanarchitecture.domain.exception;

/**
 * Exceção de negócio — produto não encontrado.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product with id " + id + " not found");
    }
}
