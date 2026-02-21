package com.example.employee.controller;

import com.example.employee.dto.LoginRequest;
import com.example.employee.dto.TokenResponse;
import com.example.employee.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TODO 5: Implementar o AuthController.
 *
 * Este controller já está parcialmente configurado. Você precisa:
 * 1. Descomentar o endpoint POST /auth/login
 * 2. O endpoint deve:
 *    - Receber um LoginRequest com @Valid
 *    - Chamar userService.authenticate(request)
 *    - Retornar ResponseEntity.ok(tokenResponse)
 *
 * TODO 8: Adicionar annotations Swagger:
 *   @Tag, @Operation, @ApiResponses
 */
@RestController
@RequestMapping("/auth")
// TODO 8: @Tag(name = "Authentication", description = "Autenticação e geração de token JWT")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // TODO 5: Descomentar e implementar o endpoint de login
    //
    // @PostMapping("/login")
    // public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
    //     return ResponseEntity.ok(userService.authenticate(request));
    // }
}
