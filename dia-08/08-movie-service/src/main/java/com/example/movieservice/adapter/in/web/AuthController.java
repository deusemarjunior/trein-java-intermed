package com.example.movieservice.adapter.in.web;

import org.springframework.web.bind.annotation.*;

// TODO 12: Implementar AuthController para autenticação JWT
//
// Endpoint:
//   POST /auth/login
//     Request Body: LoginRequest (email, password)
//     Response: TokenResponse (token, type, expiresIn)
//
// O que fazer:
//   1. Injetar UserJpaRepository e JwtUtil
//   2. Receber LoginRequest com @Valid e @RequestBody
//   3. Buscar o usuário pelo email
//   4. Validar a senha com BCrypt (PasswordEncoder)
//   5. Gerar o token JWT com JwtUtil
//   6. Retornar TokenResponse
//
// Exemplo:
//
// @RestController
// @RequestMapping("/auth")
// @Tag(name = "Auth", description = "Autenticação")
// public class AuthController {
//
//     private final UserJpaRepository userRepository;
//     private final JwtUtil jwtUtil;
//     private final PasswordEncoder passwordEncoder;
//
//     @PostMapping("/login")
//     public ResponseEntity<TokenResponse> login(@Valid @RequestBody LoginRequest request) {
//         UserEntity user = userRepository.findByEmail(request.email())
//                 .orElseThrow(InvalidCredentialsException::new);
//
//         if (!passwordEncoder.matches(request.password(), user.getPassword())) {
//             throw new InvalidCredentialsException();
//         }
//
//         String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
//         return ResponseEntity.ok(new TokenResponse(token, "Bearer", 3600));
//     }
// }

@RestController
@RequestMapping("/auth")
public class AuthController {

    // TODO 12: Implementar login com JWT

}
