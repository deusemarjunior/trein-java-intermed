package com.example.employee.config;

import com.example.employee.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * TODO 6: Configurar o SecurityFilterChain.
 *
 * O filterChain deve:
 * 1. Desabilitar CSRF (API stateless)
 * 2. Permitir H2 Console (frameOptions sameOrigin)
 * 3. Definir SessionCreationPolicy.STATELESS
 * 4. Configurar autorizações:
 *    - /auth/** → permitAll
 *    - /h2-console/** → permitAll
 *    - /swagger-ui/**, /v3/api-docs/** → permitAll
 *    - GET /api/employees/** → permitAll
 *    - Demais → authenticated
 * 5. Adicionar JwtAuthenticationFilter antes de UsernamePasswordAuthenticationFilter
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO 6: Implementar a configuração do SecurityFilterChain
        //
        // return http
        //         .csrf(AbstractHttpConfigurer::disable)
        //         .headers(headers -> headers
        //                 .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
        //         .sessionManagement(session ->
        //                 session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        //         .authorizeHttpRequests(auth -> auth
        //                 .requestMatchers("/auth/**").permitAll()
        //                 .requestMatchers("/h2-console/**").permitAll()
        //                 .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
        //                         "/v3/api-docs/**").permitAll()
        //                 .requestMatchers(HttpMethod.GET, "/api/employees/**").permitAll()
        //                 .anyRequest().authenticated())
        //         .addFilterBefore(jwtAuthenticationFilter,
        //                 UsernamePasswordAuthenticationFilter.class)
        //         .build();

        // Configuração temporária — permite tudo (remover após implementar TODO 6)
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
