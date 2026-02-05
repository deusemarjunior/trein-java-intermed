package com.example.solid.lsp.before;

/**
 * ❌ VIOLAÇÃO DO LSP
 * 
 * Square herda de Rectangle, mas quebra o contrato!
 * 
 * Problema: Um quadrado É UM retângulo matematicamente,
 * mas em programação OO, Square viola o princípio de substituição:
 * - setWidth() também altera height
 * - setHeight() também altera width
 * 
 * Isso quebra expectativas de quem usa Rectangle!
 */
public class Square extends Rectangle {
    
    public Square(int side) {
        super(side, side);
    }
    
    @Override
    public void setWidth(int width) {
        this.width = width;
        this.height = width;  // ⚠️ Altera altura também!
    }
    
    @Override
    public void setHeight(int height) {
        this.width = height;   // ⚠️ Altera largura também!
        this.height = height;
    }
}
