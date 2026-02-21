package com.example.employee.client;

import com.example.employee.config.FeignConfig;
import com.example.employee.dto.ExternalDepartmentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * TODO 1: Criar o Feign Client para o serviço externo de departamentos.
 *
 * Este client já está parcialmente configurado. Você precisa:
 * 1. Verificar se a annotation @FeignClient está correta
 *    (name, url vindo de ${department.api.url}, configuration)
 * 2. Descomentar os métodos abaixo
 * 3. Conferir que os paths correspondem à API externa
 *
 * API externa (simulada):
 *   GET /api/departments      → Lista todos
 *   GET /api/departments/{id} → Busca por ID
 */
@FeignClient(
        name = "department-service",
        url = "${department.api.url}",
        configuration = FeignConfig.class
)
public interface ExternalDepartmentClient {

    // TODO 1: Descomentar os métodos abaixo

    // @GetMapping("/api/departments/{id}")
    // ExternalDepartmentResponse findById(@PathVariable Long id);

    // @GetMapping("/api/departments")
    // List<ExternalDepartmentResponse> findAll();
}
