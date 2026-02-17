package com.example.ddd.domain.valueobject;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * VALUE OBJECT: CPF
 * - Imutável
 * - Com validação de formato
 * - Formatação automática
 */
@Getter
@EqualsAndHashCode
public class CPF {
    private final String value;

    public CPF(String value) {
        String cleaned = value.replaceAll("[^0-9]", "");
        if (cleaned.length() != 11) {
            throw new IllegalArgumentException("CPF deve ter 11 dígitos");
        }
        this.value = cleaned;
    }

    public String formatted() {
        return value.replaceAll("(\\d{3})(\\d{3})(\\d{3})(\\d{2})", "$1.$2.$3-$4");
    }

    @Override
    public String toString() {
        return formatted();
    }
}
