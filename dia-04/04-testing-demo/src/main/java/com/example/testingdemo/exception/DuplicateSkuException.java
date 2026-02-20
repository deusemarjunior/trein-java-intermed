package com.example.testingdemo.exception;

public class DuplicateSkuException extends RuntimeException {

    public DuplicateSkuException(String sku) {
        super("Já existe um produto com o SKU: " + sku);
    }
}
