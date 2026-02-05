package com.example.patterns.factory;

import org.springframework.stereotype.Component;

@Component
public class WhatsAppNotification implements Notification {
    @Override
    public void send(String message, String recipient) {
        System.out.println("💬 WHATSAPP para " + recipient + ": " + message);
    }
    
    @Override
    public NotificationType getType() {
        return NotificationType.WHATSAPP;
    }
}
