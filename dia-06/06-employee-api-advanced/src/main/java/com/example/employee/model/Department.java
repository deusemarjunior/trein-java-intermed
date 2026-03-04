package com.example.employee.model;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Entidade Department — departamento da empresa.
 */
@Entity
@Table(name = "departments")
public class Department implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 10)
    private String code;

    public Department() {}

    public Department(String name, String code) {
        this.name = name;
        this.code = code;
    }

    // ── Getters e Setters ────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
