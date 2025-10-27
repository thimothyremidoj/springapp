package com.example.todo.service;

import com.example.todo.entity.Task;
import com.example.todo.entity.Task.Status;
import com.example.todo.entity.Task.Priority;
import com.example.todo.entity.User;
import com.example.todo.repository.TaskRepository;
import com.example.todo.repository.UserRepository;
import com.example.todo.repository.ReminderRepository;
import com.example.todo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private ReminderRepository reminderRepository;
    
    @Autowired
    private NotificationRepository notificationRepository;

    private User getCurrentAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new RuntimeException("No authenticated user found");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }

    public List<Task> getUserTasks(Long userId) {
        return taskRepository.findByUserIdAndArchivedFalse(userId);
    }

    public Page<Task> getUserTasks(Long userId, Pageable pageable) {
        return taskRepository.findByUserIdAndArchivedFalse(userId, pageable);
    }

    @Cacheable("tasks")
    @SuppressWarnings("unused")
    public List<Task> getAllTasks() {
        User currentUser = getCurrentAuthenticatedUser();
        return taskRepository.findByUserIdAndArchivedFalse(currentUser.getId());
    }

    public Page<Task> getAllTasksPaginated(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    @CacheEvict(value = "tasks", allEntries = true)
    public Task createTaskWithoutAuth(Task task) {
        User currentUser = getCurrentAuthenticatedUser();
        task.setUser(currentUser);
        return taskRepository.save(task);
    }
    
    @CacheEvict(value = "tasks", allEntries = true)
    public Task createTaskForUser(Task task, String username) {
        User currentUser = getCurrentAuthenticatedUser();
        // Verify the username matches the authenticated user (security check)
        if (!currentUser.getUsername().equals(username)) {
            throw new RuntimeException("Access denied: Cannot create task for different user");
        }
        task.setUser(currentUser);
        return taskRepository.save(task);
    }

    public Optional<Task> getTaskById(Long taskId, Long userId) {
        return taskRepository.findById(taskId)
                .filter(task -> task.getUser().getId().equals(userId));
    }

    public Task createTask(Task task, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        task.setUser(user);
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task taskDetails, Long userId) {
        Task task = getTaskById(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setPriority(taskDetails.getPriority());
        task.setDueDate(taskDetails.getDueDate());
        task.setUpdatedAt(LocalDateTime.now());
        
        return taskRepository.save(task);
    }

    public Task updateTaskStatus(Long taskId, Status status, Long userId) {
        Task task = getTaskById(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId, Long userId) {
        Task task = getTaskById(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }

    public Task archiveTask(Long taskId, Long userId) {
        Task task = getTaskById(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setArchived(true);
        return taskRepository.save(task);
    }

    public List<Task> searchTasks(String keyword, Long userId) {
        return taskRepository.searchTasks(userId, keyword);
    }

    public Page<Task> searchTasks(String keyword, Long userId, Pageable pageable) {
        return taskRepository.searchTasks(userId, keyword, pageable);
    }

    public List<Task> getTasksByStatus(Status status, Long userId) {
        return taskRepository.findByUserIdAndStatus(userId, status);
    }

    public Page<Task> getTasksByStatus(Status status, Long userId, Pageable pageable) {
        return taskRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    public List<Task> getTasksByPriority(Priority priority, Long userId) {
        return taskRepository.findByUserIdAndPriority(userId, priority);
    }

    public Page<Task> getTasksByPriority(Priority priority, Long userId, Pageable pageable) {
        return taskRepository.findByUserIdAndPriority(userId, priority, pageable);
    }

    public List<Task> getTasksByDateRange(LocalDateTime start, LocalDateTime end, Long userId) {
        return taskRepository.findTasksByDateRange(userId, start, end);
    }

    public List<Task> getOverdueTasks(Long userId) {
        return taskRepository.findByUserIdAndDueDateBeforeAndStatusNot(
                userId, LocalDateTime.now(), Status.COMPLETED);
    }

    public Optional<Task> getTaskByIdWithoutAuth(Long taskId) {
        User currentUser = getCurrentAuthenticatedUser();
        return taskRepository.findById(taskId)
                .filter(task -> task.getUser().getId().equals(currentUser.getId()));
    }

    public Task updateTaskWithoutAuth(Long taskId, Task taskDetails) {
        User currentUser = getCurrentAuthenticatedUser();
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
        
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setPriority(taskDetails.getPriority());
        task.setDueDate(taskDetails.getDueDate());
        task.setUpdatedAt(LocalDateTime.now());
        
        return taskRepository.save(task);
    }

    public Task updateTaskStatusWithoutAuth(Long taskId, Status status) {
        User currentUser = getCurrentAuthenticatedUser();
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
        task.setStatus(status);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Transactional
    public void deleteTaskWithoutAuth(Long taskId) {
        User currentUser = getCurrentAuthenticatedUser();
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
        
        // Delete related records first to avoid foreign key constraint violations
        reminderRepository.deleteByTaskId(taskId);
        notificationRepository.deleteByTaskId(taskId);
        
        taskRepository.delete(task);
    }

    public List<Task> getTasksByDateRangeWithoutAuth(LocalDateTime start, LocalDateTime end) {
        User currentUser = getCurrentAuthenticatedUser();
        return taskRepository.findAll().stream()
                .filter(task -> task.getUser().getId().equals(currentUser.getId()) &&
                               task.getDueDate() != null && 
                               !task.isArchived() &&
                               !task.getDueDate().isBefore(start) && 
                               !task.getDueDate().isAfter(end))
                .collect(Collectors.toList());
    }

    public List<Task> getTasksByPriorityWithoutAuth(Priority priority) {
        User currentUser = getCurrentAuthenticatedUser();
        return taskRepository.findAll().stream()
                .filter(task -> task.getUser().getId().equals(currentUser.getId()) &&
                               !task.isArchived() && task.getPriority() == priority)
                .collect(Collectors.toList());
    }
    
    public Task archiveTaskWithoutAuth(Long taskId) {
        User currentUser = getCurrentAuthenticatedUser();
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
        task.setArchived(true);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }
    
    public Task unarchiveTaskWithoutAuth(Long taskId) {
        User currentUser = getCurrentAuthenticatedUser();
        Task task = taskRepository.findById(taskId)
                .filter(t -> t.getUser().getId().equals(currentUser.getId()))
                .orElseThrow(() -> new RuntimeException("Task not found or access denied"));
        task.setArchived(false);
        task.setUpdatedAt(LocalDateTime.now());
        return taskRepository.save(task);
    }
    
    public List<Task> getArchivedTasksWithoutAuth() {
        User currentUser = getCurrentAuthenticatedUser();
        return taskRepository.findAll().stream()
                .filter(task -> task.isArchived() && task.getUser().getId().equals(currentUser.getId()))
                .collect(Collectors.toList());
    }
    
    public Page<Task> searchTasksAdvanced(String keyword, String status, String priority, Pageable pageable) {
        User currentUser = getCurrentAuthenticatedUser();
        return taskRepository.findAll(pageable).map(task -> {
            if (task == null || !task.getUser().getId().equals(currentUser.getId()) || task.isArchived()) {
                return null;
            }
            
            boolean matches = true;
            
            if (keyword != null && !keyword.isEmpty()) {
                String title = task.getTitle() != null ? task.getTitle().toLowerCase() : "";
                String description = task.getDescription() != null ? task.getDescription().toLowerCase() : "";
                matches = title.contains(keyword.toLowerCase()) || description.contains(keyword.toLowerCase());
            }
            
            if (status != null && !status.isEmpty() && task.getStatus() != null) {
                matches = matches && task.getStatus().name().equalsIgnoreCase(status);
            }
            
            if (priority != null && !priority.isEmpty() && task.getPriority() != null) {
                matches = matches && task.getPriority().name().equalsIgnoreCase(priority);
            }
            
            return matches ? task : null;
        }).map(task -> task);
    }
    
    public Page<Task> getTasksWithFilters(String status, String priority, Pageable pageable) {
        User currentUser = getCurrentAuthenticatedUser();
        return taskRepository.findAll(pageable).map(task -> {
            if (task == null || !task.getUser().getId().equals(currentUser.getId()) || task.isArchived()) {
                return null;
            }
            
            boolean matches = true;
            
            if (status != null && !status.isEmpty() && task.getStatus() != null) {
                matches = task.getStatus().name().equalsIgnoreCase(status);
            }
            
            if (priority != null && !priority.isEmpty() && task.getPriority() != null) {
                matches = matches && task.getPriority().name().equalsIgnoreCase(priority);
            }
            
            return matches ? task : null;
        }).map(task -> task);
    }
    
    public Page<Task> getUserTasksWithFilters(Long userId, String status, String priority, Pageable pageable) {
        Page<Task> userTasks = taskRepository.findByUserIdAndArchivedFalse(userId, pageable);
        return userTasks.map(task -> {
            if (task == null) {
                return null;
            }
            
            boolean matches = true;
            
            if (status != null && !status.isEmpty() && task.getStatus() != null) {
                matches = task.getStatus().name().equalsIgnoreCase(status);
            }
            
            if (priority != null && !priority.isEmpty() && task.getPriority() != null) {
                matches = matches && task.getPriority().name().equalsIgnoreCase(priority);
            }
            
            return matches ? task : null;
        }).map(task -> task);
    }
    
    public Task saveTask(Task task) {
        return taskRepository.save(task);
    }
}