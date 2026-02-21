package com.example.employee.controller;

import com.example.employee.dto.ExternalDepartmentResponse;
import com.example.employee.service.DepartmentIntegrationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller para consulta de departamentos externos via Feign.
 * Dependências: TODO 1 (Feign Client) e TODO 3 (Resilience4j).
 *
 * TODO 8: Adicionar annotations Swagger (@Tag, @Operation, @ApiResponse)
 * Após TODO 6: Adicionar @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
 */
@RestController
@RequestMapping("/api/external-departments")
// TODO 8: @Tag(name = "External Departments", description = "Consulta via serviço externo (Feign + Resilience4j)")
public class ExternalDepartmentController {

    private final DepartmentIntegrationService departmentService;

    public ExternalDepartmentController(DepartmentIntegrationService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public ResponseEntity<List<ExternalDepartmentResponse>> findAll() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExternalDepartmentResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(departmentService.findById(id));
    }
}
