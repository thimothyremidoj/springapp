package com.example.todo.repository;

import com.example.todo.entity.Reminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    List<Reminder> findByTaskId(Long taskId);
    List<Reminder> findByReminderTimeBeforeAndSentFalse(LocalDateTime time);
    void deleteByTaskId(Long taskId);
}
