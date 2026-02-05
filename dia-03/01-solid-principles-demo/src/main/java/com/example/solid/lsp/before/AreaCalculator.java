package com.example.solid.lsp.before;

/**
 * ❌ Demonstração do problema
 * 
 * Este código funciona com Rectangle, mas QUEBRA com Square!
 */
public class AreaCalculator {
    
    public static void testRectangle(Rectangle rect) {
        rect.setWidth(5);
        rect.setHeight(4);
        
        int expectedArea = 20;  // 5 * 4 = 20
        int actualArea = rect.getArea();
        
        System.out.println("Expected: " + expectedArea);
        System.out.println("Actual: " + actualArea);
        
        if (expectedArea != actualArea) {
            System.out.println("❌ VIOLAÇÃO DO LSP! Square não pode substituir Rectangle!");
        } else {
            System.out.println("✅ OK");
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Testando Rectangle:");
        testRectangle(new Rectangle(0, 0));  // ✅ Funciona: área = 20
        
        System.out.println("\nTestando Square:");
        testRectangle(new Square(0));         // ❌ Quebra: área = 16!
    }
}
