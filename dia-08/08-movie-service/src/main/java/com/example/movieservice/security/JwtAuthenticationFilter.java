package com.example.movieservice.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// TODO 12: Implementar JwtAuthenticationFilter
//
// Esta classe intercepta TODAS as requisições e valida o token JWT.
//
// O que fazer:
//   1. Extrair o header "Authorization" da requisição
//   2. Verificar se começa com "Bearer "
//   3. Extrair o token (remover "Bearer ")
//   4. Validar com JwtUtil.isTokenValid(token)
//   5. Se válido, extrair email e role do token
//   6. Criar UsernamePasswordAuthenticationToken e setar no SecurityContextHolder
//   7. Chamar filterChain.doFilter(request, response) para continuar
//
// Exemplo:
//
// @Override
// protected void doFilterInternal(HttpServletRequest request,
//                                 HttpServletResponse response,
//                                 FilterChain filterChain)
//         throws ServletException, IOException {
//
//     String authHeader = request.getHeader("Authorization");
//
//     if (authHeader != null && authHeader.startsWith("Bearer ")) {
//         String token = authHeader.substring(7);
//
//         if (jwtUtil.isTokenValid(token)) {
//             String email = jwtUtil.getEmailFromToken(token);
//             String role = jwtUtil.getRoleFromToken(token);
//
//             var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
//             var authentication = new UsernamePasswordAuthenticationToken(
//                     email, null, authorities);
//
//             SecurityContextHolder.getContext().setAuthentication(authentication);
//         }
//     }
//
//     filterChain.doFilter(request, response);
// }

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // TODO 12: Implementar a validação do token JWT aqui

        filterChain.doFilter(request, response);
    }
}
