package com.example.patterns;

import com.example.patterns.builder.Order;
import com.example.patterns.builder.User;
import com.example.patterns.factory.NotificationFactory;
import com.example.patterns.factory.NotificationType;
import com.example.patterns.singleton.AppLogger;
import com.example.patterns.singleton.ConfigurationManager;
import com.example.patterns.singleton.DatabaseConnectionPool;
import com.example.patterns.strategy.DiscountService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.math.BigDecimal;

@SpringBootApplication
@RequiredArgsConstructor
public class DesignPatternsDemoApplication {

    private final DiscountService discountService;
    private final NotificationFactory notificationFactory;

    public static void main(String[] args) {
        SpringApplication.run(DesignPatternsDemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo() {
        return args -> {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("DESIGN PATTERNS DEMO");
            System.out.println("=".repeat(80) + "\n");

            demoStrategy();
            demoFactory();
            demoBuilder();
            demoSingleton();

            System.out.println("\n" + "=".repeat(80));
        };
    }

    private void demoStrategy() {
        System.out.println("🎯 STRATEGY PATTERN - Descontos");
        System.out.println("-".repeat(80));
        
        BigDecimal price = new BigDecimal("100.00");
        
        String[] customerTypes = {"VIP", "REGULAR", "PREMIUM", "BLACK_FRIDAY"};
        for (String type : customerTypes) {
            BigDecimal finalPrice = discountService.applyDiscount(price, type);
            String desc = discountService.getDiscountDescription(type);
            System.out.println(desc + " → R$ " + finalPrice);
        }
        System.out.println();
    }

    private void demoFactory() {
        System.out.println("🏭 FACTORY PATTERN - Notificações");
        System.out.println("-".repeat(80));
        
        String message = "Seu pedido foi confirmado!";
        String recipient = "user@example.com";
        
        for (NotificationType type : NotificationType.values()) {
            var notification = notificationFactory.createNotification(type);
            notification.send(message, recipient);
        }
        System.out.println();
    }

    private void demoBuilder() {
        System.out.println("🔨 BUILDER PATTERN - Construção de objetos");
        System.out.println("-".repeat(80));
        
        User user = User.builder()
                .name("João Silva")
                .email("joao@example.com")
                .phone("11999999999")
                .active(true)
                .build();
        
        System.out.println("User criado: " + user);
        
        Order order = Order.builder()
                .description("Notebook Dell")
                .total(new BigDecimal("3500.00"))
                .customerName("João Silva")
                .build();
        
        System.out.println("Order criado: " + order);
        System.out.println();
    }

    private void demoSingleton() {
        System.out.println("🔒 SINGLETON PATTERN - Instância única");
        System.out.println("-".repeat(80));

        // Bill Pugh (Holder Pattern) - Recomendado
        ConfigurationManager config1 = ConfigurationManager.getInstance();
        ConfigurationManager config2 = ConfigurationManager.getInstance();
        System.out.println("Mesma instância? " + (config1 == config2));  // true
        System.out.println("Config: " + config1);

        // Eager Initialization
        DatabaseConnectionPool pool1 = DatabaseConnectionPool.getInstance();
        DatabaseConnectionPool pool2 = DatabaseConnectionPool.getInstance();
        System.out.println("Mesma instância? " + (pool1 == pool2));  // true
        pool1.getConnection();
        pool1.releaseConnection();

        // Double-Checked Locking
        AppLogger logger1 = AppLogger.getInstance();
        AppLogger logger2 = AppLogger.getInstance();
        System.out.println("Mesma instância? " + (logger1 == logger2));  // true
        logger1.info("Singleton pattern demonstrado com sucesso!");

        System.out.println();
    }
}
