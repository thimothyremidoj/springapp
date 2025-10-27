package com.example.todo.service;

import com.example.todo.entity.Reminder;
import com.example.todo.entity.Task;
import com.example.todo.repository.ReminderRepository;
import com.example.todo.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReminderService {
    
    @Autowired
    private ReminderRepository reminderRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    public Reminder createReminder(Long taskId, LocalDateTime reminderTime) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        
        Reminder reminder = new Reminder();
        reminder.setTask(task);
        reminder.setReminderTime(reminderTime);
        return reminderRepository.save(reminder);
    }
    
    public List<Reminder> getRemindersByTask(Long taskId) {
        return reminderRepository.findByTaskId(taskId);
    }
    
    public List<Reminder> getPendingReminders() {
        return reminderRepository.findByReminderTimeBeforeAndSentFalse(LocalDateTime.now());
    }
    
    public void deleteReminder(Long id) {
        reminderRepository.deleteById(id);
    }
}
