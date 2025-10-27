package com.example.todo.dto;

import jakarta.validation.constraints.Email;

public class UpdateProfileRequest {
    
    @Email
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
