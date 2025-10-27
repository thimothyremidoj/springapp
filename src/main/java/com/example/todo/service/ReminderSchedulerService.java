package com.example.todo.service;

import com.example.todo.entity.Task;
import com.example.todo.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// @Service
public class ReminderSchedulerService {
    
    private static final Logger logger = LoggerFactory.getLogger(ReminderSchedulerService.class);
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private EmailService emailService;
    
    @Scheduled(fixedRate = 3600000) // Run every hour
    public void checkForUpcomingTasks() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime next24Hours = now.plusHours(24);
        
        List<Task> upcomingTasks = taskRepository.findAll().stream()
            .filter(task -> task.getDueDate() != null &&
                           !task.getStatus().equals(Task.Status.COMPLETED) &&
                           task.getDueDate().isAfter(now) &&
                           task.getDueDate().isBefore(next24Hours))
            .collect(Collectors.toList());
        
        for (Task task : upcomingTasks) {
            notificationService.createTaskReminderNotification(task.getUser(), task);
            emailService.sendTaskReminderEmail(task.getUser(), task);
        }
        
        logger.info("Processed {} upcoming task reminders", upcomingTasks.size());
    }
    
    @Scheduled(fixedRate = 3600000) // Run every hour
    @Transactional
    public void checkForOverdueTasks() {
        LocalDateTime now = LocalDateTime.now();
        
        List<Task> overdueTasks = taskRepository.findAll().stream()
            .filter(task -> task.getDueDate() != null &&
                           !task.getStatus().equals(Task.Status.COMPLETED) &&
                           task.getDueDate().isBefore(now))
            .collect(Collectors.toList());
        
        for (Task task : overdueTasks) {
            notificationService.createOverdueTaskNotification(task.getUser(), task);
            emailService.sendOverdueTaskEmail(task.getUser(), task);
        }
        
        logger.info("Processed {} overdue task notifications", overdueTasks.size());
    }
}