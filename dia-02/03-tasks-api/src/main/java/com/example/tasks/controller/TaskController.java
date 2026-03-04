package com.example.tasks.controller;

import com.example.tasks.dto.CreateTaskRequest;
import com.example.tasks.dto.TaskResponse;
import com.example.tasks.dto.UpdateTaskRequest;
import com.example.tasks.model.Priority;
import com.example.tasks.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Gerenciamento de tarefas com paginação, filtros e estatísticas")
public class TaskController {
    
    private final TaskService taskService;
    
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    @Operation(summary = "Listar tarefas", description = "Lista todas as tarefas com paginação e ordenação")
    @GetMapping
    public ResponseEntity<Page<TaskResponse>> getAllTasks(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "id") String sort,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<TaskResponse> tasks = taskService.findAllPaged(pageable);
        return ResponseEntity.ok(tasks);
    }
    
    @Operation(summary = "Busca avançada", description = "Busca tarefas com filtros: palavra-chave, prioridade, status")
    @GetMapping("/search")
    public ResponseEntity<Page<TaskResponse>> searchTasks(
        @RequestParam(required = false) String keyword,
        @RequestParam(required = false) Priority priority,
        @RequestParam(required = false) Boolean completed,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "createdAt") String sort,
        @RequestParam(defaultValue = "DESC") String direction
    ) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));
        
        Page<TaskResponse> tasks = taskService.searchTasks(keyword, priority, completed, pageable);
        return ResponseEntity.ok(tasks);
    }
    
    @Operation(summary = "Buscar tarefa por ID")
    @ApiResponse(responseCode = "200", description = "Tarefa encontrada")
    @ApiResponse(responseCode = "404", description = "Tarefa não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskResponse task = taskService.findById(id);
        return ResponseEntity.ok(task);
    }
    
    @Operation(summary = "Listar tarefas pendentes")
    @GetMapping("/pending")
    public ResponseEntity<List<TaskResponse>> getPendingTasks() {
        List<TaskResponse> tasks = taskService.findPending();
        return ResponseEntity.ok(tasks);
    }
    
    @Operation(summary = "Listar tarefas concluídas")
    @GetMapping("/completed")
    public ResponseEntity<List<TaskResponse>> getCompletedTasks() {
        List<TaskResponse> tasks = taskService.findCompleted();
        return ResponseEntity.ok(tasks);
    }
    
    @Operation(summary = "Filtrar por prioridade")
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskResponse>> getTasksByPriority(@PathVariable Priority priority) {
        List<TaskResponse> tasks = taskService.findByPriority(priority);
        return ResponseEntity.ok(tasks);
    }
    
    @Operation(summary = "Listar tarefas atrasadas")
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        List<TaskResponse> tasks = taskService.findOverdue();
        return ResponseEntity.ok(tasks);
    }
    
    @Operation(summary = "Estatísticas", description = "Retorna contadores de tarefas por status")
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        Map<String, Long> stats = taskService.getStatistics();
        return ResponseEntity.ok(stats);
    }
    
    @Operation(summary = "Criar tarefa")
    @ApiResponse(responseCode = "201", description = "Tarefa criada")
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse created = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @Operation(summary = "Atualizar tarefa")
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
        @PathVariable Long id,
        @Valid @RequestBody UpdateTaskRequest request
    ) {
        TaskResponse updated = taskService.update(id, request);
        return ResponseEntity.ok(updated);
    }
    
    @Operation(summary = "Atualização parcial")
    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> partialUpdateTask(
        @PathVariable Long id,
        @RequestBody UpdateTaskRequest request
    ) {
        TaskResponse updated = taskService.update(id, request);
        return ResponseEntity.ok(updated);
    }
    
    @Operation(summary = "Concluir tarefa")
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long id) {
        TaskResponse completed = taskService.complete(id);
        return ResponseEntity.ok(completed);
    }
    
    @Operation(summary = "Deletar tarefa")
    @ApiResponse(responseCode = "204", description = "Tarefa deletada")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
