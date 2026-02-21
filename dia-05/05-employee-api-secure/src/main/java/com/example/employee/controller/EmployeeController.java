package com.example.employee.controller;

import com.example.employee.dto.EmployeeRequest;
import com.example.employee.dto.EmployeeResponse;
import com.example.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller de funcionários.
 *
 * TODO 8: Adicionar annotations Swagger/OpenAPI:
 * - @Tag(name = "Employees", description = "...")
 * - @Operation(summary = "...", description = "...") em cada endpoint
 * - @ApiResponse(responseCode = "...", description = "...") em cada endpoint
 * - @Parameter(description = "...") nos @PathVariable
 *
 * Após implementar TODO 6, adicionar @PreAuthorize nos endpoints de escrita:
 * - POST, PUT, DELETE → @PreAuthorize("hasRole('ADMIN')")
 */
@RestController
@RequestMapping("/api/employees")
// TODO 8: @Tag(name = "Employees", description = "Operações de gerenciamento de funcionários")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    // TODO 8: @Operation e @ApiResponse
    @GetMapping
    public ResponseEntity<List<EmployeeResponse>> findAll() {
        return ResponseEntity.ok(employeeService.findAll());
    }

    // TODO 8: @Operation e @ApiResponses
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(employeeService.findById(id));
    }

    // TODO 8: @Operation e @ApiResponses
    // Após TODO 6: @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<EmployeeResponse> create(
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(employeeService.create(request));
    }

    // TODO 8: @Operation e @ApiResponses
    // Após TODO 6: @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(employeeService.update(id, request));
    }

    // TODO 8: @Operation e @ApiResponses
    // Após TODO 6: @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
