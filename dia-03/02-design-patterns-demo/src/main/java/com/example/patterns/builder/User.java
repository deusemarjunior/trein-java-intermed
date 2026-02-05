package com.example.patterns.builder;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Builder
public class User {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private boolean active;
    
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
