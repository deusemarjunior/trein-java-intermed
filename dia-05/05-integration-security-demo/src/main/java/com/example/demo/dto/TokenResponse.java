package com.example.demo.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Token JWT de autenticação")
public record TokenResponse(
        @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiJ9...")
        String token
) {}
