package com.example.tasks.dto;

import com.example.tasks.model.Priority;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record UpdateTaskRequest(
    @Size(min = 3, max = 200, message = "Título deve ter entre 3 e 200 caracteres")
    String title,
    
    @Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
    String description,
    
    Priority priority,
    
    LocalDateTime dueDate,
    
    Boolean completed
) {}
