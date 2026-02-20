package com.example.cleanarchitecture.domain.exception;

/**
 * Exceção de negócio — SKU duplicado.
 */
public class DuplicateSkuException extends RuntimeException {

    public DuplicateSkuException(String sku) {
        super("Product with SKU '" + sku + "' already exists");
    }
}
