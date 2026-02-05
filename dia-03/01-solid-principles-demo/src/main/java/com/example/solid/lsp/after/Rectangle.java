package com.example.solid.lsp.after;

import lombok.Getter;
import lombok.Setter;

/**
 * ✅ Rectangle como implementação independente
 */
@Getter
@Setter
public class Rectangle implements Shape {
    private int width;
    private int height;
    
    public Rectangle(int width, int height) {
        this.width = width;
        this.height = height;
    }
    
    @Override
    public int getArea() {
        return width * height;
    }
    
    @Override
    public String getName() {
        return "Rectangle";
    }
}
