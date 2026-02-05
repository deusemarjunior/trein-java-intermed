package com.example.patterns.factory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class NotificationFactory {
    private final List<Notification> notifications;
    
    public Notification createNotification(NotificationType type) {
        return notifications.stream()
                .filter(n -> n.getType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tipo não suportado: " + type));
    }
}
