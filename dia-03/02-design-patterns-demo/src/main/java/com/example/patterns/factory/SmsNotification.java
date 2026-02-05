package com.example.patterns.factory;

import org.springframework.stereotype.Component;

@Component
public class SmsNotification implements Notification {
    @Override
    public void send(String message, String recipient) {
        System.out.println("📱 SMS para " + recipient + ": " + message);
    }
    
    @Override
    public NotificationType getType() {
        return NotificationType.SMS;
    }
}
