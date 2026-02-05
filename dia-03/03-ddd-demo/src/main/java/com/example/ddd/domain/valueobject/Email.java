package com.example.ddd.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * VALUE OBJECT: Email
 * - Imutável
 * - Com validação
 */
@Getter
@EqualsAndHashCode
public class Email {
    private final String value;
    
    public Email(String value) {
        if (value == null || !value.contains("@")) {
            throw new IllegalArgumentException("Email inválido");
        }
        this.value = value;
    }
    
    @Override
    public String toString() {
        return value;
    }
}
