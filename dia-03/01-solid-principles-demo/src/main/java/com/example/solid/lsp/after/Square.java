package com.example.solid.lsp.after;

import lombok.Getter;
import lombok.Setter;

/**
 * ✅ Square como implementação independente
 * 
 * Não herda de Rectangle, evitando a violação do LSP
 */
@Getter
@Setter
public class Square implements Shape {
    private int side;
    
    public Square(int side) {
        this.side = side;
    }
    
    @Override
    public int getArea() {
        return side * side;
    }
    
    @Override
    public String getName() {
        return "Square";
    }
}
