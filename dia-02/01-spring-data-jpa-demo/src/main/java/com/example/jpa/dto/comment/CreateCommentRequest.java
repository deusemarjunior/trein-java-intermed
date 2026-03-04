package com.example.jpa.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCommentRequest(
    @NotBlank(message = "Texto é obrigatório")
    @Size(min = 2, message = "Comentário deve ter no mínimo 2 caracteres")
    String text,
    
    @NotBlank(message = "Autor é obrigatório")
    @Size(max = 100, message = "Nome do autor deve ter no máximo 100 caracteres")
    String author
) {}
