package com.example.todo.controller;

import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin")
@Tag(name = "Admin", description = "Admin management APIs")
public class AdminController {
    
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    @Operation(summary = "Get all users")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userRepository.findAll();
            users.forEach(user -> user.setPassword(null));
            return ResponseEntity.ok(users);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to get users");
        }
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return userRepository.findById(id)
                    .map(user -> {
                        user.setPassword(null);
                        return ResponseEntity.ok((Object) user);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to get user");
        }
    }

    @DeleteMapping("/users/{id}")
    @Operation(summary = "Delete user")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete user");
        }
    }

    @PutMapping("/users/{id}/role")
    @Operation(summary = "Update user role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, 
                                             @RequestParam User.Role role) {
        try {
            return userRepository.findById(id)
                    .map(user -> {
                        user.setRole(role);
                        User updatedUser = userRepository.save(user);
                        updatedUser.setPassword(null);
                        return ResponseEntity.ok((Object) updatedUser);
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to update user role");
        }
    }
}
