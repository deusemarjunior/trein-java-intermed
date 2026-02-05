package com.example.patterns.factory;

import org.springframework.stereotype.Component;

@Component
public class EmailNotification implements Notification {
    @Override
    public void send(String message, String recipient) {
        System.out.println("📧 EMAIL para " + recipient + ": " + message);
    }
    
    @Override
    public NotificationType getType() {
        return NotificationType.EMAIL;
    }
}
