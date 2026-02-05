package com.example.jpa.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tags")
public class Tag {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String name;
    
    @Column(length = 7)
    private String color; // Hex color, ex: #FF5733
    
    // Muitas Tags podem estar em muitos Posts
    @ManyToMany(mappedBy = "tags")
    private Set<Post> posts = new HashSet<>();
    
    // Constructors
    public Tag() {}
    
    public Tag(String name, String color) {
        this.name = name;
        this.color = color;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getColor() {
        return color;
    }
    
    public void setColor(String color) {
        this.color = color;
    }
    
    public Set<Post> getPosts() {
        return posts;
    }
    
    public void setPosts(Set<Post> posts) {
        this.posts = posts;
    }
}
