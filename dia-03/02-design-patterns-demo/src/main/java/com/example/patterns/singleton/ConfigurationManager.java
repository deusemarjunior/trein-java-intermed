package com.example.patterns.singleton;

/**
 * 🔒 SINGLETON PATTERN - Lazy Initialization com Bill Pugh (Holder Pattern)
 * 
 * Garante uma única instância usando classe interna estática.
 * A JVM garante lazy loading e thread-safety.
 * 
 * ⭐ Implementação RECOMENDADA para Singleton clássico
 */
public class ConfigurationManager {

    private String databaseUrl = "jdbc:mysql://localhost:3306/db";
    private String appName = "Design Patterns Demo";
    private int maxConnections = 10;

    // Construtor privado - ninguém pode instanciar
    private ConfigurationManager() {
        System.out.println("    [ConfigurationManager] Instância criada (Holder Pattern)");
    }

    // Classe interna estática - lazy loading garantido pela JVM
    private static class Holder {
        private static final ConfigurationManager INSTANCE = new ConfigurationManager();
    }

    public static ConfigurationManager getInstance() {
        return Holder.INSTANCE;
    }

    public String getDatabaseUrl() {
        return databaseUrl;
    }

    public String getAppName() {
        return appName;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    @Override
    public String toString() {
        return "ConfigurationManager{app=" + appName + ", db=" + databaseUrl + ", maxConn=" + maxConnections + "}";
    }
}
