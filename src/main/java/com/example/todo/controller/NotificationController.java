package com.example.todo.controller;

import com.example.todo.entity.Notification;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import com.example.todo.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(originPatterns = "http://localhost:3000", maxAge = 3600)
@RestController
@RequestMapping("/notifications")
@Tag(name = "Notifications", description = "Notification management APIs")
public class NotificationController {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserRepository userRepository;
    
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("No authenticated user found");
        }
        String username = authentication.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
    
    @GetMapping("/unread")
    @Operation(summary = "Get unread notifications")
    public ResponseEntity<List<Notification>> getUnreadNotifications() {
        try {
            User currentUser = getCurrentUser();
            List<Notification> notifications = notificationService.getUnreadNotifications(currentUser.getId());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping
    @Operation(summary = "Get all notifications")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        try {
            User currentUser = getCurrentUser();
            List<Notification> notifications = notificationService.getAllNotifications(currentUser.getId());
            return ResponseEntity.ok(notifications);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @GetMapping("/count")
    @Operation(summary = "Get unread notification count")
    public ResponseEntity<Long> getUnreadCount() {
        try {
            User currentUser = getCurrentUser();
            long count = notificationService.getUnreadCount(currentUser.getId());
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @PutMapping("/{id}/read")
    @Operation(summary = "Mark notification as read")
    public ResponseEntity<Void> markAsRead(@PathVariable Long id) {
        try {
            notificationService.markAsRead(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
    
    @PutMapping("/read-all")
    @Operation(summary = "Mark all notifications as read")
    public ResponseEntity<Void> markAllAsRead() {
        try {
            User currentUser = getCurrentUser();
            notificationService.markAllAsRead(currentUser.getId());
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        }
    }
}