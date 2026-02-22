package com.example.cleanarchitecture.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

/**
 * Implementação do validador de SKU.
 * Formato: 3 letras maiúsculas + hífen + 4 dígitos (ex: NOT-0001, MOU-1234).
 */
public class SkuValidator implements ConstraintValidator<ValidSku, String> {

    private static final Pattern SKU_PATTERN = Pattern.compile("^[A-Z]{3}-\\d{4}$");

    @Override
    public boolean isValid(String sku, ConstraintValidatorContext context) {
        if (sku == null || sku.isBlank()) {
            return false;
        }
        return SKU_PATTERN.matcher(sku).matches();
    }
}
