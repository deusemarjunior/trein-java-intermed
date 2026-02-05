package com.example.solid.srp.after;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * ✅ APLICANDO SRP
 * Responsabilidade ÚNICA: Orquestrar a criação de usuários
 * 
 * Este serviço agora apenas ORQUESTRA as outras classes,
 * delegando cada responsabilidade específica para a classe apropriada.
 */
@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserValidator validator;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository repository;
    private final EmailService emailService;
    
    public void createUser(String name, String email, String password) {
        // 1. Validar dados
        validator.validate(name, email, password);
        
        // 2. Criptografar senha
        String encodedPassword = passwordEncoder.encode(password);
        
        // 3. Criar usuário
        User user = new User(name, email, encodedPassword);
        
        // 4. Salvar no banco
        repository.save(user);
        
        // 5. Enviar email de boas-vindas
        emailService.sendWelcomeEmail(email, name);
        
        System.out.println("✅ Usuário criado com sucesso!");
    }
}
