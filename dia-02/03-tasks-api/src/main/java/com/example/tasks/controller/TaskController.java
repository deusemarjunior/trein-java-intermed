package com.example.tasks.controller;

import com.example.tasks.dto.CreateTaskRequest;
import com.example.tasks.dto.TaskResponse;
import com.example.tasks.dto.UpdateTaskRequest;
import com.example.tasks.model.Priority;
import com.example.tasks.service.TaskService;
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
public class TaskController {
    
    private final TaskService taskService;
    
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }
    
    // GET /api/tasks - Lista todas as tarefas (com paginação opcional)
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
    
    // GET /api/tasks/search - Busca avançada com filtros
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
    
    // GET /api/tasks/{id} - Busca tarefa por ID
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {
        TaskResponse task = taskService.findById(id);
        return ResponseEntity.ok(task);
    }
    
    // GET /api/tasks/pending - Lista tarefas pendentes
    @GetMapping("/pending")
    public ResponseEntity<List<TaskResponse>> getPendingTasks() {
        List<TaskResponse> tasks = taskService.findPending();
        return ResponseEntity.ok(tasks);
    }
    
    // GET /api/tasks/completed - Lista tarefas concluídas
    @GetMapping("/completed")
    public ResponseEntity<List<TaskResponse>> getCompletedTasks() {
        List<TaskResponse> tasks = taskService.findCompleted();
        return ResponseEntity.ok(tasks);
    }
    
    // GET /api/tasks/priority/{priority} - Filtra por prioridade
    @GetMapping("/priority/{priority}")
    public ResponseEntity<List<TaskResponse>> getTasksByPriority(@PathVariable Priority priority) {
        List<TaskResponse> tasks = taskService.findByPriority(priority);
        return ResponseEntity.ok(tasks);
    }
    
    // GET /api/tasks/overdue - Lista tarefas atrasadas
    @GetMapping("/overdue")
    public ResponseEntity<List<TaskResponse>> getOverdueTasks() {
        List<TaskResponse> tasks = taskService.findOverdue();
        return ResponseEntity.ok(tasks);
    }
    
    // GET /api/tasks/statistics - Estatísticas
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Long>> getStatistics() {
        Map<String, Long> stats = taskService.getStatistics();
        return ResponseEntity.ok(stats);
    }
    
    // POST /api/tasks - Cria nova tarefa
    @PostMapping
    public ResponseEntity<TaskResponse> createTask(@Valid @RequestBody CreateTaskRequest request) {
        TaskResponse created = taskService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    // PUT /api/tasks/{id} - Atualiza tarefa completamente
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
        @PathVariable Long id,
        @Valid @RequestBody UpdateTaskRequest request
    ) {
        TaskResponse updated = taskService.update(id, request);
        return ResponseEntity.ok(updated);
    }
    
    // PATCH /api/tasks/{id} - Atualização parcial
    @PatchMapping("/{id}")
    public ResponseEntity<TaskResponse> partialUpdateTask(
        @PathVariable Long id,
        @RequestBody UpdateTaskRequest request
    ) {
        TaskResponse updated = taskService.update(id, request);
        return ResponseEntity.ok(updated);
    }
    
    // PATCH /api/tasks/{id}/complete - Marca tarefa como concluída
    @PatchMapping("/{id}/complete")
    public ResponseEntity<TaskResponse> completeTask(@PathVariable Long id) {
        TaskResponse completed = taskService.complete(id);
        return ResponseEntity.ok(completed);
    }
    
    // DELETE /api/tasks/{id} - Deleta tarefa
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
