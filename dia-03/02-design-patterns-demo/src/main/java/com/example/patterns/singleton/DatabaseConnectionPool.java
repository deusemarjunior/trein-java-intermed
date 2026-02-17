package com.example.patterns.singleton;

/**
 * 🔒 SINGLETON PATTERN - Eager Initialization
 * 
 * Instância criada na carga da classe.
 * Thread-safe e simples, porém criado mesmo se não for usado.
 */
public class DatabaseConnectionPool {

    // Instância criada imediatamente na carga da classe
    private static final DatabaseConnectionPool INSTANCE = new DatabaseConnectionPool();

    private int poolSize = 5;
    private int activeConnections = 0;

    // Construtor privado
    private DatabaseConnectionPool() {
        System.out.println("    [DatabaseConnectionPool] Pool inicializado (Eager Init)");
    }

    public static DatabaseConnectionPool getInstance() {
        return INSTANCE;
    }

    public synchronized void getConnection() {
        if (activeConnections >= poolSize) {
            throw new IllegalStateException("Pool esgotado!");
        }
        activeConnections++;
        System.out.println("    Conexão obtida (" + activeConnections + "/" + poolSize + ")");
    }

    public synchronized void releaseConnection() {
        if (activeConnections > 0) {
            activeConnections--;
            System.out.println("    Conexão liberada (" + activeConnections + "/" + poolSize + ")");
        }
    }

    public int getActiveConnections() {
        return activeConnections;
    }

    public int getPoolSize() {
        return poolSize;
    }

    @Override
    public String toString() {
        return "DatabaseConnectionPool{active=" + activeConnections + "/" + poolSize + "}";
    }
}
