package com.example.tasks.repository;

import com.example.tasks.model.Priority;
import com.example.tasks.model.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Query Methods
    List<Task> findByCompleted(boolean completed);
    
    Page<Task> findByCompleted(boolean completed, Pageable pageable);
    
    List<Task> findByPriority(Priority priority);
    
    List<Task> findByTitleContainingIgnoreCase(String keyword);
    
    List<Task> findByDueDateBefore(LocalDateTime date);
    
    // Custom JPQL queries
    @Query("SELECT t FROM Task t WHERE t.completed = :completed")
    Page<Task> findByCompletedPaged(@Param("completed") boolean completed, Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE t.priority = :priority AND t.completed = false")
    List<Task> findPendingByPriority(@Param("priority") Priority priority);
    
    @Query("SELECT t FROM Task t WHERE t.dueDate < :date AND t.completed = false")
    List<Task> findOverdueTasks(@Param("date") LocalDateTime date);
    
    // Busca complexa com filtros dinâmicos
    @Query("SELECT t FROM Task t WHERE " +
           "(:keyword IS NULL OR LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%'))) AND " +
           "(:priority IS NULL OR t.priority = :priority) AND " +
           "(:completed IS NULL OR t.completed = :completed)")
    Page<Task> searchTasks(
        @Param("keyword") String keyword,
        @Param("priority") Priority priority,
        @Param("completed") Boolean completed,
        Pageable pageable
    );
    
    // Estatísticas
    long countByCompleted(boolean completed);
    
    long countByPriority(Priority priority);
}
