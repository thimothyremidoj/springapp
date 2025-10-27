package com.example.todo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

// @Component
@Profile("!test")
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public void run(String... args) {
        System.out.println("DataInitializer: Starting user creation...");
        
        // Update admin user password if exists, create if not
        User admin = userRepository.findByUsername("admin").orElse(null);
        if (admin != null) {
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmailNotificationsEnabled(true);
            userRepository.save(admin);
            System.out.println("DataInitializer: Admin password updated successfully");
        } else {
            admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@todoapp.com");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setRole(User.Role.ADMIN);
            admin.setEmailNotificationsEnabled(true);
            userRepository.save(admin);
            System.out.println("DataInitializer: Admin user created successfully");
        }
        System.out.println("Admin password hash: " + admin.getPassword());

        // Update regular user password if exists, create if not
        User user = userRepository.findByUsername("user").orElse(null);
        if (user != null) {
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmailNotificationsEnabled(true);
            userRepository.save(user);
            System.out.println("DataInitializer: Regular user password updated successfully");
        } else {
            user = new User();
            user.setUsername("user");
            user.setEmail("user@todoapp.com");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setRole(User.Role.USER);
            user.setEmailNotificationsEnabled(true);
            userRepository.save(user);
            System.out.println("DataInitializer: Regular user created successfully");
        }
        
        System.out.println("DataInitializer: User creation completed");
    }
}
