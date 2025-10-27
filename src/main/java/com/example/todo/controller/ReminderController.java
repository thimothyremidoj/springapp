package com.example.todo.controller;

import com.example.todo.dto.ReminderRequest;
import com.example.todo.entity.Reminder;
import com.example.todo.service.ReminderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/reminders")
@Tag(name = "Reminders", description = "Reminder management APIs")
public class ReminderController {
    
    @Autowired
    private ReminderService reminderService;
    
    @PostMapping
    @Operation(summary = "Create reminder")
    public ResponseEntity<?> createReminder(@Valid @RequestBody ReminderRequest request) {
        try {
            Reminder reminder = reminderService.createReminder(request.getTaskId(), request.getReminderTime());
            return ResponseEntity.ok(reminder);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to create reminder");
        }
    }
    
    @GetMapping("/task/{taskId}")
    @Operation(summary = "Get reminders by task")
    public ResponseEntity<?> getRemindersByTask(@PathVariable Long taskId) {
        try {
            return ResponseEntity.ok(reminderService.getRemindersByTask(taskId));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to get reminders");
        }
    }
    
    @GetMapping("/pending")
    @Operation(summary = "Get pending reminders")
    public ResponseEntity<?> getPendingReminders() {
        try {
            return ResponseEntity.ok(reminderService.getPendingReminders());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to get pending reminders");
        }
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete reminder")
    public ResponseEntity<?> deleteReminder(@PathVariable Long id) {
        try {
            reminderService.deleteReminder(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to delete reminder");
        }
    }
}
