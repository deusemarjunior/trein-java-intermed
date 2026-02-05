package com.example.solid.lsp.after;

import java.util.List;

/**
 * ✅ APLICANDO LSP
 * 
 * Agora todas as implementações de Shape podem ser substituídas
 * sem quebrar o comportamento esperado!
 */
public class AreaCalculator {
    
    public static void calculateArea(Shape shape) {
        int area = shape.getArea();
        System.out.println(shape.getName() + " área: " + area);
    }
    
    public static int calculateTotalArea(List<Shape> shapes) {
        return shapes.stream()
                .mapToInt(Shape::getArea)
                .sum();
    }
    
    public static void main(String[] args) {
        // ✅ Todas as implementações funcionam corretamente
        Rectangle rectangle = new Rectangle(5, 4);
        Square square = new Square(5);
        Circle circle = new Circle(5);
        
        System.out.println("Calculando áreas individuais:");
        calculateArea(rectangle);  // 20
        calculateArea(square);     // 25
        calculateArea(circle);     // 78
        
        System.out.println("\nCalculando área total:");
        List<Shape> shapes = List.of(rectangle, square, circle);
        int total = calculateTotalArea(shapes);
        System.out.println("Total: " + total);  // 123
    }
}
