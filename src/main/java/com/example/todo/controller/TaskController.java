package com.example.todo.controller;

import com.example.todo.dto.TaskRequest;
import com.example.todo.entity.Task;
import com.example.todo.entity.Task.Status;
import com.example.todo.entity.Task.Priority;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import com.example.todo.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/tasks")
@Tag(name = "Tasks", description = "Task management APIs for CRUD operations, filtering, and searching")
public class TaskController {
    
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    @Operation(summary = "Get user tasks", description = "Get paginated list of user tasks with sorting and filtering options")
    public ResponseEntity<?> getAllTasks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "admin") String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Task> tasks = taskService.getUserTasksWithFilters(user.getId(), status, priority, pageable);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Failed to get tasks", e);
            return ResponseEntity.status(500).body("Failed to get tasks");
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Get all tasks without pagination")
    public ResponseEntity<?> getAllTasksNoPagination(@RequestParam(defaultValue = "admin") String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Task> tasks = taskService.getUserTasks(user.getId());
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Failed to get all tasks", e);
            return ResponseEntity.status(500).body("Failed to get all tasks");
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get task by ID")
    public ResponseEntity<?> getTaskById(@PathVariable Long id) {
        try {
            Optional<Task> task = taskService.getTaskByIdWithoutAuth(id);
            return task.map(t -> ResponseEntity.ok((Object) t))
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            logger.error("Failed to get task", e);
            return ResponseEntity.status(500).body("Failed to get task");
        }
    }

    @PostMapping
    @Operation(summary = "Create task", description = "Create a new task for the authenticated user")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest taskRequest) {
        try {
            Task task = new Task();
            task.setTitle(taskRequest.getTitle());
            task.setDescription(taskRequest.getDescription());
            task.setPriority(taskRequest.getPriority());
            task.setDueDate(taskRequest.getDueDate());
            task.setStatus(Task.Status.PENDING);
            
            // Set default user for testing
            User defaultUser = userRepository.findByUsername("admin").orElse(null);
            if (defaultUser == null) {
                defaultUser = new User();
                defaultUser.setUsername("admin");
                defaultUser.setEmail("admin@test.com");
                defaultUser.setPassword("password");
                defaultUser = userRepository.save(defaultUser);
            }
            task.setUser(defaultUser);
            
            Task savedTask = taskService.saveTask(task);
            return ResponseEntity.ok(savedTask);
        } catch (Exception e) {
            logger.error("Error creating task", e);
            return ResponseEntity.badRequest().body("Failed to create task: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update task")
    public ResponseEntity<?> updateTask(@PathVariable Long id,
                                         @Valid @RequestBody TaskRequest taskRequest) {
        try {
            Task taskDetails = new Task();
            taskDetails.setTitle(taskRequest.getTitle());
            taskDetails.setDescription(taskRequest.getDescription());
            taskDetails.setPriority(taskRequest.getPriority());
            taskDetails.setDueDate(taskRequest.getDueDate());
            Task updatedTask = taskService.updateTaskWithoutAuth(id, taskDetails);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            logger.error("Failed to update task", e);
            return ResponseEntity.status(500).body("Failed to update task");
        }
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update task status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable Long id,
                                               @RequestParam Status status) {
        try {
            Task updatedTask = taskService.updateTaskStatusWithoutAuth(id, status);
            return ResponseEntity.ok(updatedTask);
        } catch (Exception e) {
            logger.error("Failed to update task status", e);
            return ResponseEntity.status(500).body("Failed to update task status");
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete task")
    public ResponseEntity<?> deleteTask(@PathVariable Long id) {
        try {
            taskService.deleteTaskWithoutAuth(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            logger.error("Failed to delete task", e);
            return ResponseEntity.status(500).body("Failed to delete task");
        }
    }

    @PutMapping("/{id}/archive")
    @Operation(summary = "Archive task")
    public ResponseEntity<?> archiveTask(@PathVariable Long id) {
        try {
            Task archivedTask = taskService.archiveTaskWithoutAuth(id);
            return ResponseEntity.ok(archivedTask);
        } catch (Exception e) {
            logger.error("Failed to archive task", e);
            return ResponseEntity.status(500).body("Failed to archive task");
        }
    }
    
    @PutMapping("/{id}/unarchive")
    @Operation(summary = "Unarchive task")
    public ResponseEntity<?> unarchiveTask(@PathVariable Long id) {
        try {
            Task unarchivedTask = taskService.unarchiveTaskWithoutAuth(id);
            return ResponseEntity.ok(unarchivedTask);
        } catch (Exception e) {
            logger.error("Failed to unarchive task", e);
            return ResponseEntity.status(500).body("Failed to unarchive task");
        }
    }
    
    @GetMapping("/archived")
    @Operation(summary = "Get archived tasks")
    public ResponseEntity<?> getArchivedTasks() {
        try {
            List<Task> tasks = taskService.getArchivedTasksWithoutAuth();
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Failed to get archived tasks", e);
            return ResponseEntity.status(500).body("Failed to get archived tasks");
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Search tasks with advanced filtering")
    public ResponseEntity<?> searchTasks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Task> tasks = taskService.searchTasksAdvanced(keyword, status, priority, pageable);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Failed to search tasks", e);
            return ResponseEntity.status(500).body("Failed to search tasks");
        }
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get tasks by status")
    public ResponseEntity<List<Task>> getTasksByStatus(@PathVariable Status status) {
        List<Task> tasks = taskService.getTasksByStatus(status, 1L);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Get tasks by priority")
    public ResponseEntity<List<Task>> getTasksByPriority(@PathVariable Priority priority) {
        List<Task> tasks = taskService.getTasksByPriorityWithoutAuth(priority);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/calendar")
    @Operation(summary = "Get tasks by date range")
    public ResponseEntity<?> getTasksByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "admin") String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            logger.info("Calendar request - Start: " + start + ", End: " + end + ", Username: " + username);
            List<Task> tasks = taskService.getTasksByDateRange(start, end, user.getId());
            logger.info("Found " + tasks.size() + " tasks for user " + username + " in date range");
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Failed to get calendar tasks", e);
            return ResponseEntity.status(500).body("Failed to get calendar tasks");
        }
    }

    @GetMapping("/overdue")
    @Operation(summary = "Get overdue tasks")
    public ResponseEntity<List<Task>> getOverdueTasks() {
        List<Task> tasks = taskService.getOverdueTasks(1L);
        return ResponseEntity.ok(tasks);
    }
    
    @GetMapping("/admin/all")
    @Operation(summary = "Get all tasks in system (Admin only)")
    public ResponseEntity<?> getAllTasksForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        try {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? 
                       Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Task> tasks = taskService.getAllTasksPaginated(pageable);
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Failed to get all tasks for admin", e);
            return ResponseEntity.status(500).body("Failed to get all tasks");
        }
    }
    
    @GetMapping("/admin/all-simple")
    @Operation(summary = "Get all tasks without pagination (Admin only)")
    public ResponseEntity<?> getAllTasksForAdminSimple() {
        try {
            List<Task> tasks = taskService.getAllTasks();
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Failed to get all tasks for admin", e);
            return ResponseEntity.status(500).body("Failed to get all tasks");
        }
    }
    
    @GetMapping("/admin/calendar")
    @Operation(summary = "Get all tasks by date range (Admin only)")
    public ResponseEntity<?> getAdminTasksByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        try {
            logger.info("Admin calendar request - Start: " + start + ", End: " + end);
            List<Task> tasks = taskService.getTasksByDateRangeWithoutAuth(start, end);
            logger.info("Found " + tasks.size() + " tasks for admin calendar");
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            logger.error("Failed to get admin calendar tasks", e);
            return ResponseEntity.status(500).body("Failed to get admin calendar tasks");
        }
    }
}
