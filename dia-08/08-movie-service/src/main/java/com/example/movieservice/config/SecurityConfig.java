package com.example.movieservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;

// TODO 12: Configurar Spring Security com JWT
//
// O que fazer:
//   1. Desabilitar CSRF (API stateless)
//   2. Definir rotas públicas: /auth/**, /swagger-ui/**, /v3/api-docs/**, /actuator/**
//   3. Definir rotas protegidas: /api/** (requer autenticação JWT)
//   4. Configurar SessionCreationPolicy.STATELESS
//   5. Adicionar JwtAuthenticationFilter antes do UsernamePasswordAuthenticationFilter
//
// ATENÇÃO: A configuração abaixo permite TUDO por padrão.
// Isso é para que o aluno consiga testar os endpoints sem JWT primeiro (TODOs 1-11).
// Ao implementar o TODO 12, restrinja as rotas conforme descrito acima.

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // TODO 12: Restringir rotas — por enquanto permite tudo
                        .anyRequest().permitAll()
                );
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
