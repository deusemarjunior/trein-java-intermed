package com.example.employee.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * TODO 7: Implementar o filtro JWT.
 *
 * Este filtro deve:
 * 1. Extrair o header "Authorization" da requisição
 * 2. Verificar se começa com "Bearer "
 * 3. Extrair o token (remover "Bearer ")
 * 4. Validar o token usando JwtUtil.isTokenValid()
 * 5. Se válido, extrair email e roles do token
 * 6. Criar um UsernamePasswordAuthenticationToken com as authorities
 *    (lembrar de prefixar com "ROLE_")
 * 7. Setar no SecurityContextHolder
 * 8. Chamar filterChain.doFilter()
 *
 * Dicas:
 * - Injetar JwtUtil no construtor
 * - Usar SecurityContextHolder.getContext().setAuthentication(...)
 * - Criar SimpleGrantedAuthority com "ROLE_" + role
 * - Se não tiver token ou for inválido, apenas chamar filterChain.doFilter()
 */
@Component
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

        // TODO 7: Implementar a lógica do filtro JWT
        //
        // String authHeader = request.getHeader("Authorization");
        //
        // if (authHeader != null && authHeader.startsWith("Bearer ")) {
        //     String token = authHeader.substring(7);
        //
        //     if (jwtUtil.isTokenValid(token)) {
        //         String email = jwtUtil.extractEmail(token);
        //         List<String> roles = jwtUtil.extractRoles(token);
        //
        //         List<SimpleGrantedAuthority> authorities = roles.stream()
        //                 .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
        //                 .toList();
        //
        //         UsernamePasswordAuthenticationToken authentication =
        //                 new UsernamePasswordAuthenticationToken(email, null, authorities);
        //
        //         SecurityContextHolder.getContext().setAuthentication(authentication);
        //     }
        // }

        filterChain.doFilter(request, response);
    }
}
