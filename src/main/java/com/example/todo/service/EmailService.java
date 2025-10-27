package com.example.todo.service;

import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

// @Service
public class EmailService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);
    
    @Autowired
    private JavaMailSender mailSender;
    
    public void sendTaskReminderEmail(User user, Task task) {
        if (!Boolean.TRUE.equals(user.isEmailNotificationsEnabled())) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Task Reminder: " + task.getTitle());
            message.setText(buildReminderEmailContent(user, task));
            mailSender.send(message);
        } catch (Exception e) {
            logger.error("Failed to send reminder email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
    
    public void sendOverdueTaskEmail(User user, Task task) {
        if (!Boolean.TRUE.equals(user.isEmailNotificationsEnabled())) {
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(user.getEmail());
            message.setSubject("Overdue Task: " + task.getTitle());
            message.setText(buildOverdueEmailContent(user, task));
            mailSender.send(message);
        } catch (Exception e) {
            logger.error("Failed to send overdue email to {}: {}", user.getEmail(), e.getMessage());
        }
    }
    
    private String buildReminderEmailContent(User user, Task task) {
        return String.format(
            "Hello %s,\n\n" +
            "This is a reminder that your task \"%s\" is due soon.\n\n" +
            "Due Date: %s\n" +
            "Priority: %s\n" +
            "Status: %s\n\n" +
            "Description: %s\n\n" +
            "Please complete this task on time.\n\n" +
            "Best regards,\n" +
            "Todo App Team",
            user.getUsername(),
            task.getTitle(),
            task.getDueDate(),
            task.getPriority(),
            task.getStatus(),
            task.getDescription() != null ? task.getDescription() : "No description"
        );
    }
    
    private String buildOverdueEmailContent(User user, Task task) {
        return String.format(
            "Hello %s,\n\n" +
            "Your task \"%s\" is now overdue.\n\n" +
            "Due Date: %s\n" +
            "Priority: %s\n" +
            "Status: %s\n\n" +
            "Description: %s\n\n" +
            "Please complete this task as soon as possible.\n\n" +
            "Best regards,\n" +
            "Todo App Team",
            user.getUsername(),
            task.getTitle(),
            task.getDueDate(),
            task.getPriority(),
            task.getStatus(),
            task.getDescription() != null ? task.getDescription() : "No description"
        );
    }
}