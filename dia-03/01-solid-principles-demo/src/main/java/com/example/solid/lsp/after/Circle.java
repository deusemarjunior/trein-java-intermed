package com.example.solid.lsp.after;

import lombok.Getter;

/**
 * ✅ Círculo como mais uma implementação
 */
@Getter
public class Circle implements Shape {
    private final int radius;
    
    public Circle(int radius) {
        this.radius = radius;
    }
    
    @Override
    public int getArea() {
        return (int) (Math.PI * radius * radius);
    }
    
    @Override
    public String getName() {
        return "Circle";
    }
}
