package com.example.todo.repository;

import com.example.todo.entity.Task;
import com.example.todo.entity.Task.Status;
import com.example.todo.entity.Task.Priority;
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
    List<Task> findByUserIdAndArchivedFalse(Long userId);
    Page<Task> findByUserIdAndArchivedFalse(Long userId, Pageable pageable);
    List<Task> findByUserIdAndStatus(Long userId, Status status);
    Page<Task> findByUserIdAndStatus(Long userId, Status status, Pageable pageable);
    List<Task> findByUserIdAndPriority(Long userId, Priority priority);
    Page<Task> findByUserIdAndPriority(Long userId, Priority priority, Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.archived = false AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Task> searchTasks(@Param("userId") Long userId, @Param("keyword") String keyword);
    
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.archived = false AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Task> searchTasks(@Param("userId") Long userId, @Param("keyword") String keyword, Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE t.user.id = :userId AND t.dueDate BETWEEN :start AND :end")
    List<Task> findTasksByDateRange(@Param("userId") Long userId, 
                                   @Param("start") LocalDateTime start, 
                                   @Param("end") LocalDateTime end);
    
    List<Task> findByUserIdAndDueDateBeforeAndStatusNot(Long userId, LocalDateTime date, Status status);
}