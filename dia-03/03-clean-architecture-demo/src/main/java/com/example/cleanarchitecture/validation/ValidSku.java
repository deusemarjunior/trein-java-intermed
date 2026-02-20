package com.example.cleanarchitecture.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Custom Validator — valida formato de SKU.
 * Formato obrigatório: 3 letras maiúsculas + hífen + 4 dígitos (ex: NOT-0001).
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = SkuValidator.class)
public @interface ValidSku {

    String message() default "SKU must follow pattern: XXX-0000 (3 uppercase letters, hyphen, 4 digits)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
