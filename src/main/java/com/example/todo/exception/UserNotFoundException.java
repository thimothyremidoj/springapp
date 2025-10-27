package com.example.todo.exception;

public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;
    
    public UserNotFoundException(String message) {
        super(message);
    }
    
    public UserNotFoundException(Long userId) {
        super("User not found with id: " + userId);
    }
}