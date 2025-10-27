package com.example.todo.exception;

public class TaskNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public TaskNotFoundException(String message) {
        super(message);
    }
    
    public TaskNotFoundException(Long taskId) {
        super("Task not found with id: " + taskId);
    }
}