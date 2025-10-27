package com.example.todo.service;

import com.example.todo.entity.Notification;
import com.example.todo.entity.Task;
import com.example.todo.entity.User;
import com.example.todo.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    public void createTaskReminderNotification(User user, Task task) {
        String message = String.format("Task '%s' is due soon on %s", task.getTitle(), task.getDueDate());
        Notification notification = new Notification(user, task, message, "REMINDER");
        notificationRepository.save(notification);
    }
    
    public void createOverdueTaskNotification(User user, Task task) {
        String message = String.format("Task '%s' is overdue (was due on %s)", task.getTitle(), task.getDueDate());
        Notification notification = new Notification(user, task, message, "OVERDUE");
        notificationRepository.save(notification);
    }
    
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }
    
    public List<Notification> getAllNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
    
    public void markAsRead(Long notificationId) {
        notificationRepository.findById(notificationId).ifPresent(notification -> {
            notification.setRead(true);
            notificationRepository.save(notification);
        });
    }
    
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
    
    public long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }
}