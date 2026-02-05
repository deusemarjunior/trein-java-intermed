package com.example.solid.isp.after;

/**
 * ✅ APLICANDO ISP
 * 
 * Developer implementa apenas as interfaces necessárias
 */
public class Developer implements Workable, HumanNeeds, Programmer, DevOps {
    
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
    
    // ✅ Não precisa implementar manageTeam() e createBudget()!
}
