package com.example.ddd.domain.entity;

import com.example.ddd.domain.valueobject.Address;
import com.example.ddd.domain.valueobject.CPF;
import com.example.ddd.domain.valueobject.Email;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * ENTITY: Customer
 * - Tem identidade única (id)
 * - Ciclo de vida rastreável
 * - Comparação por ID
 */
@Getter
public class Customer {
    private final Long id;
    private String name;
    private final Email email;
    private final CPF cpf;
    private Address address;
    private final List<Long> orderIds = new ArrayList<>();
    private final LocalDateTime createdAt;

    public Customer(Long id, String name, Email email, CPF cpf, Address address) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        this.id = id;
        this.name = name;
        this.email = email;
        this.cpf = cpf;
        this.address = address;
        this.createdAt = LocalDateTime.now();
    }

    public void updateName(String newName) {
        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        this.name = newName;
    }

    public void updateAddress(Address newAddress) {
        this.address = newAddress;
    }

    public void addOrder(Long orderId) {
        orderIds.add(orderId);
    }

    public List<Long> getOrderIds() {
        return Collections.unmodifiableList(orderIds);
    }

    // Comparação por ID (Entity)
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer customer)) return false;
        return id != null && id.equals(customer.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public String toString() {
        return "Customer{id=" + id + ", name=" + name + ", email=" + email + ", cpf=" + cpf + "}";
    }
}
