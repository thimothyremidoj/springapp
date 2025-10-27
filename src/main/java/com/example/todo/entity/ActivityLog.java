package com.example.todo.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "activity_logs")
public class ActivityLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String action;
    
    @Column(nullable = false)
    private String entityType;
    
    private Long entityId;
    
    private String username;
    
    @Column(columnDefinition = "TEXT")
    private String details;
    
    @Column(nullable = false)
    private LocalDateTime timestamp;
    
    public ActivityLog() {
        this.timestamp = LocalDateTime.now();
    }
    
    public ActivityLog(String action, String entityType, Long entityId, String username, String details) {
        this();
        this.action = action;
        this.entityType = entityType;
        this.entityId = entityId;
        this.username = username;
        this.details = details;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public Long getEntityId() { return entityId; }
    public void setEntityId(Long entityId) { this.entityId = entityId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getDetails() { return details; }
    public void setDetails(String details) { this.details = details; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}