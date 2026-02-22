package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

@Schema(description = "Dados de login")
public record LoginRequest(
        @Schema(description = "Email do usuário", example = "admin@email.com")
        @NotBlank(message = "Email é obrigatório")
        @Email(message = "Email deve ter formato válido")
        String email,

        @Schema(description = "Senha do usuário", example = "admin123")
        @NotBlank(message = "Senha é obrigatória")
        String password
) {}
