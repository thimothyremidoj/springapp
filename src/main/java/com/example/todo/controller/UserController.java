package com.example.todo.controller;

import com.example.todo.dto.ChangePasswordRequest;
import com.example.todo.dto.UpdateProfileRequest;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User management APIs")
public class UserController {
    
    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @GetMapping("/me")
    @Operation(summary = "Get current user")
    public ResponseEntity<?> getCurrentUser(@RequestParam(defaultValue = "admin") String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to get user");
        }
    }

    @PutMapping("/me/password")
    @Operation(summary = "Change password")
    public ResponseEntity<?> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                          @RequestParam(defaultValue = "admin") String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
                return ResponseEntity.badRequest().body("Current password is incorrect");
            }
            
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            
            return ResponseEntity.ok("Password changed successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to change password");
        }
    }

    @PutMapping("/me")
    @Operation(summary = "Update profile")
    public ResponseEntity<?> updateProfile(@Valid @RequestBody UpdateProfileRequest request,
                                            @RequestParam(defaultValue = "admin") String username) {
        try {
            User user = userRepository.findByUsername(username)
                    .orElse(null);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            
            if (request.getEmail() != null) {
                if (userRepository.existsByEmail(request.getEmail()) && !user.getEmail().equals(request.getEmail())) {
                    return ResponseEntity.badRequest().body("Email already in use");
                }
                user.setEmail(request.getEmail());
            }
            
            User updatedUser = userRepository.save(user);
            updatedUser.setPassword(null);
            return ResponseEntity.ok(updatedUser);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update profile");
        }
    }
}
