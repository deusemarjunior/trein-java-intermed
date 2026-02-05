package com.example.solid.isp.before;

/**
 * ❌ VIOLAÇÃO DO ISP (Interface Segregation Principle)
 * 
 * Interface muito grande - força implementações desnecessárias
 */
public interface Worker {
    void work();
    void eat();
    void sleep();
    void attendMeeting();
    void writeCode();
    void reviewCode();
    void deployToProduction();
    void manageTeam();
    void createBudget();
}
