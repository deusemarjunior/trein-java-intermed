package com.example.ddd;

import com.example.ddd.domain.aggregate.Order;
import com.example.ddd.domain.aggregate.OrderItem;
import com.example.ddd.domain.valueobject.Address;
import com.example.ddd.domain.valueobject.Email;
import com.example.ddd.domain.valueobject.Money;
import com.example.ddd.domain.service.OrderPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@RequiredArgsConstructor
public class DddDemoApplication {

    private final OrderPricingService pricingService;

    public static void main(String[] args) {
        SpringApplication.run(DddDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo() {
        return args -> {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("DOMAIN-DRIVEN DESIGN DEMO");
            System.out.println("=".repeat(80) + "\n");

            // Value Objects
            Money price = new Money("100.00", "BRL");
            Email email = new Email("customer@example.com");
            Address address = new Address("Rua A", "São Paulo", "SP", "01000-000");
            
            System.out.println("💎 VALUE OBJECTS:");
            System.out.println("Money: " + price);
            System.out.println("Email: " + email);
            System.out.println("Address: " + address);
            System.out.println();

            // Aggregate Root: Order
            Order order = new Order(email, address);
            order.addItem(new OrderItem("Notebook", new Money("3000.00", "BRL"), 1));
            order.addItem(new OrderItem("Mouse", new Money("50.00", "BRL"), 2));
            
            System.out.println("📦 AGGREGATE (Order):");
            System.out.println(order);
            System.out.println();

            // Domain Service
            Money total = pricingService.calculateTotal(order);
            System.out.println("⚙️ DOMAIN SERVICE:");
            System.out.println("Total calculado: " + total);
            System.out.println();

            System.out.println("=".repeat(80) + "\n");
        };
    }
}
