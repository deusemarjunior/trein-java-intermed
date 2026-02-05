package com.example.solid.isp.before;

/**
 * ❌ VIOLAÇÃO DO ISP
 * 
 * Developer é forçado a implementar métodos que não fazem sentido!
 */
public class Developer implements Worker {
    
    @Override
    public void work() {
        System.out.println("Desenvolvendo...");
    }
    
    @Override
    public void eat() {
        System.out.println("Comendo...");
    }
    
    @Override
    public void sleep() {
        System.out.println("Dormindo...");
    }
    
    @Override
    public void attendMeeting() {
        System.out.println("Participando de reunião...");
    }
    
    @Override
    public void writeCode() {
        System.out.println("Escrevendo código...");
    }
    
    @Override
    public void reviewCode() {
        System.out.println("Revisando código...");
    }
    
    @Override
    public void deployToProduction() {
        System.out.println("Fazendo deploy...");
    }
    
    // ⚠️ Developer não gerencia equipe!
    @Override
    public void manageTeam() {
        throw new UnsupportedOperationException("Developer não gerencia equipe");
    }
    
    // ⚠️ Developer não cria orçamento!
    @Override
    public void createBudget() {
        throw new UnsupportedOperationException("Developer não cria orçamento");
    }
}
