package com.example.employee.service;

import com.example.employee.dto.LoginRequest;
import com.example.employee.dto.TokenResponse;
import com.example.employee.exception.InvalidCredentialsException;
import com.example.employee.model.User;
import com.example.employee.repository.UserRepository;
import com.example.employee.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço de autenticação — já implementado.
 * Usado pelo AuthController (TODO 5).
 */
@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public TokenResponse authenticate(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException(
                        "Credenciais inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Credenciais inválidas");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRoles());
        return new TokenResponse(token);
    }
}
