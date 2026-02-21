package com.example.demo.controller;

import com.example.demo.dto.DepartmentResponse;
import com.example.demo.service.DepartmentIntegrationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/departments")
@Tag(name = "Departments", description = "Consulta de departamentos via serviço externo (Feign + Resilience4j)")
public class DepartmentController {

    private final DepartmentIntegrationService departmentService;

    public DepartmentController(DepartmentIntegrationService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Listar departamentos",
            description = "Consulta departamentos no serviço externo. Requer autenticação.")
    @ApiResponse(responseCode = "200", description = "Lista retornada com sucesso (pode ser fallback)")
    public ResponseEntity<List<DepartmentResponse>> findAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    @Operation(summary = "Buscar departamento por ID",
            description = "Consulta departamento no serviço externo. Requer autenticação.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Departamento encontrado (ou fallback)"),
            @ApiResponse(responseCode = "401", description = "Não autenticado")
    })
    public ResponseEntity<DepartmentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.findById(id));
    }
}
