package com.example.tasks.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(Long id) {
        super("Tarefa não encontrada com ID: " + id);
    }
}
