package com.example.todo.dto;

import com.example.todo.entity.Task.Priority;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDateTime;
// import com.example.todo.validation.FutureDateTime;

public class TaskRequest {
    @NotBlank(message = "Title is required")
    @Size(min = 1, max = 100, message = "Title must be between 1 and 100 characters")
    private String title;
    
    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
    
    @NotNull(message = "Priority is required")
    private Priority priority = Priority.MEDIUM;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    private LocalDateTime dueDate;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    
    public void setPriority(Priority priority) {
        this.priority = priority;
    }
    
    public void setPriority(String priority) {
        if (priority != null && !priority.isEmpty()) {
            try {
                this.priority = Priority.valueOf(priority.toUpperCase());
            } catch (IllegalArgumentException e) {
                this.priority = Priority.MEDIUM; // Default fallback
            }
        }
    }

    public LocalDateTime getDueDate() { return dueDate; }
    public void setDueDate(LocalDateTime dueDate) { this.dueDate = dueDate; }
}