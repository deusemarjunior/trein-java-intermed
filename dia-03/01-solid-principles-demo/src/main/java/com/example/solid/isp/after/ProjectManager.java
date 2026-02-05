package com.example.solid.isp.after;

/**
 * ✅ APLICANDO ISP
 * 
 * ProjectManager implementa apenas as interfaces necessárias
 */
public class ProjectManager implements Workable, HumanNeeds, Manager {
    
    @Override
    public void work() {
        System.out.println("Gerenciando projetos...");
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
    public void manageTeam() {
        System.out.println("Gerenciando equipe...");
    }
    
    @Override
    public void attendMeeting() {
        System.out.println("Participando de reunião...");
    }
    
    @Override
    public void createBudget() {
        System.out.println("Criando orçamento...");
    }
    
    // ✅ Não precisa implementar writeCode(), reviewCode(), deployToProduction()!
}
