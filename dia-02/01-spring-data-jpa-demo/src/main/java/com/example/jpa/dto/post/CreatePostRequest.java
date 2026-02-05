package com.example.jpa.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreatePostRequest(
    @NotBlank(message = "Título é obrigatório")
    @Size(min = 5, max = 200, message = "Título deve ter entre 5 e 200 caracteres")
    String title,
    
    @NotBlank(message = "Conteúdo é obrigatório")
    @Size(min = 10, message = "Conteúdo deve ter no mínimo 10 caracteres")
    String content,
    
    @NotBlank(message = "Autor é obrigatório")
    @Size(max = 100, message = "Nome do autor deve ter no máximo 100 caracteres")
    String author,
    
    Set<Long> tagIds
) {}
