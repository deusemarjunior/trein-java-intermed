package com.example.tasks.service;

import com.example.tasks.dto.CreateTaskRequest;
import com.example.tasks.dto.TaskResponse;
import com.example.tasks.dto.UpdateTaskRequest;
import com.example.tasks.exception.TaskNotFoundException;
import com.example.tasks.model.Priority;
import com.example.tasks.model.Task;
import com.example.tasks.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class TaskService {
    
    private final TaskRepository taskRepository;
    
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }
    
    public TaskResponse findById(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        return TaskResponse.from(task);
    }
    
    public List<TaskResponse> findAll() {
        return taskRepository.findAll().stream()
            .map(TaskResponse::from)
            .toList();
    }
    
    public Page<TaskResponse> findAllPaged(Pageable pageable) {
        return taskRepository.findAll(pageable)
            .map(TaskResponse::from);
    }
    
    public Page<TaskResponse> searchTasks(String keyword, Priority priority, Boolean completed, Pageable pageable) {
        return taskRepository.searchTasks(keyword, priority, completed, pageable)
            .map(TaskResponse::from);
    }
    
    public List<TaskResponse> findPending() {
        return taskRepository.findByCompleted(false).stream()
            .map(TaskResponse::from)
            .toList();
    }
    
    public List<TaskResponse> findCompleted() {
        return taskRepository.findByCompleted(true).stream()
            .map(TaskResponse::from)
            .toList();
    }
    
    public List<TaskResponse> findByPriority(Priority priority) {
        return taskRepository.findByPriority(priority).stream()
            .map(TaskResponse::from)
            .toList();
    }
    
    public List<TaskResponse> findOverdue() {
        return taskRepository.findOverdueTasks(LocalDateTime.now()).stream()
            .map(TaskResponse::from)
            .toList();
    }
    
    @Transactional
    public TaskResponse create(CreateTaskRequest request) {
        Task task = new Task(request.title(), request.description(), request.priority());
        task.setDueDate(request.dueDate());
        
        Task saved = taskRepository.save(task);
        return TaskResponse.from(saved);
    }
    
    @Transactional
    public TaskResponse update(Long id, UpdateTaskRequest request) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        
        if (request.title() != null) {
            task.setTitle(request.title());
        }
        if (request.description() != null) {
            task.setDescription(request.description());
        }
        if (request.priority() != null) {
            task.setPriority(request.priority());
        }
        if (request.dueDate() != null) {
            task.setDueDate(request.dueDate());
        }
        if (request.completed() != null) {
            if (request.completed()) {
                task.complete();
            } else {
                task.uncomplete();
            }
        }
        
        Task updated = taskRepository.save(task);
        return TaskResponse.from(updated);
    }
    
    @Transactional
    public TaskResponse complete(Long id) {
        Task task = taskRepository.findById(id)
            .orElseThrow(() -> new TaskNotFoundException(id));
        
        task.complete();
        Task updated = taskRepository.save(task);
        return TaskResponse.from(updated);
    }
    
    @Transactional
    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new TaskNotFoundException(id);
        }
        taskRepository.deleteById(id);
    }
    
    public Map<String, Long> getStatistics() {
        long total = taskRepository.count();
        long completed = taskRepository.countByCompleted(true);
        long pending = taskRepository.countByCompleted(false);
        long urgent = taskRepository.countByPriority(Priority.URGENT);
        
        return Map.of(
            "total", total,
            "completed", completed,
            "pending", pending,
            "urgent", urgent
        );
    }
}
