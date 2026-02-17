package com.example.patterns.singleton;

/**
 * 🔒 SINGLETON PATTERN - Double-Checked Locking
 * 
 * Thread-safe com lazy initialization usando volatile + synchronized.
 * Demonstra a técnica clássica de double-checked locking.
 */
public class AppLogger {

    private static volatile AppLogger instance;

    private AppLogger() {
        System.out.println("    [AppLogger] Logger inicializado (Double-Checked Locking)");
    }

    public static AppLogger getInstance() {
        if (instance == null) {                    // 1ª verificação (sem lock)
            synchronized (AppLogger.class) {
                if (instance == null) {             // 2ª verificação (com lock)
                    instance = new AppLogger();
                }
            }
        }
        return instance;
    }

    public void info(String message) {
        System.out.println("    [INFO] " + message);
    }

    public void warn(String message) {
        System.out.println("    [WARN] " + message);
    }

    public void error(String message) {
        System.out.println("    [ERROR] " + message);
    }
}
