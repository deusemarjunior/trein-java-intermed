package com.example.patterns.factory;

public interface Notification {
    void send(String message, String recipient);
    NotificationType getType();
}
