package com.example.solid.lsp.before;

import lombok.Data;

/**
 * ❌ VIOLAÇÃO DO LSP (Liskov Substitution Principle)
 * 
 * Classe base: Rectangle
 */
@Data
public class Rectangle {
    protected int width;
    protected int height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    public int getArea() {
        return width * height;
    }
}
