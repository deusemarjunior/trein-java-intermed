package com.example.testingdemo.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Produto não encontrado com id: " + id);
    }

    public ProductNotFoundException(String message) {
        super(message);
    }
}
