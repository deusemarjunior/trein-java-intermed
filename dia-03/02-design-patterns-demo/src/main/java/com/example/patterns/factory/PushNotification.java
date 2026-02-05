package com.example.patterns.factory;

import org.springframework.stereotype.Component;

@Component
public class PushNotification implements Notification {
    @Override
    public void send(String message, String recipient) {
        System.out.println("🔔 PUSH para " + recipient + ": " + message);
    }
    
    @Override
    public NotificationType getType() {
        return NotificationType.PUSH;
    }
}
