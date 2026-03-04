package com.example.tasks.dto;

import com.example.tasks.model.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record CreateTaskRequest(
    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 200, message = "Título deve ter entre 3 e 200 caracteres")
    String title,
    
    @Size(max = 2000, message = "Descrição deve ter no máximo 2000 caracteres")
    String description,
    
    @NotNull(message = "Prioridade é obrigatória")
    Priority priority,
    
    LocalDateTime dueDate
) {}
