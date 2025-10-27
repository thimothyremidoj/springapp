package com.example.todo.controller;

import com.example.todo.dto.JwtResponse;
import com.example.todo.dto.LoginRequest;
import com.example.todo.dto.RegisterRequest;
import com.example.todo.entity.User;
import com.example.todo.repository.UserRepository;
import com.example.todo.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(originPatterns = "*", maxAge = 3600)
@RestController
@RequestMapping("/auth")
@Tag(name = "Authentication", description = "User authentication and registration APIs")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user with username and password")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            User user = userRepository.findByUsername(loginRequest.getUsername())
                    .orElse(null);

            if (user == null) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
            
            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }
            String token = jwtUtil.generateToken(user.getUsername(), user.getRole().name());
            return ResponseEntity.ok(new JwtResponse(token, user.getUsername(), user.getEmail(), user.getRole().name()));
        } catch (Exception e) {
            System.out.println("Login error: " + e.getMessage());
            return ResponseEntity.status(500).body("Login failed");
        }
    }

    @PostMapping("/register")
    @Operation(summary = "User registration", description = "Register a new user account")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest signUpRequest) {
        try {
            System.out.println("Registration attempt for username: " + signUpRequest.getUsername());
            System.out.println("Registration attempt for email: " + signUpRequest.getEmail());
            
            if (userRepository.existsByUsername(signUpRequest.getUsername())) {
                System.out.println("Username already exists: " + signUpRequest.getUsername());
                return ResponseEntity.badRequest().body("Username is already taken");
            }

            if (userRepository.existsByEmail(signUpRequest.getEmail())) {
                System.out.println("Email already exists: " + signUpRequest.getEmail());
                return ResponseEntity.badRequest().body("Email is already in use");
            }

            User user = new User(signUpRequest.getUsername(), 
                               signUpRequest.getEmail(),
                               passwordEncoder.encode(signUpRequest.getPassword()));
            user.setEmailNotificationsEnabled(true);

            User savedUser = userRepository.save(user);
            System.out.println("User registered successfully with ID: " + savedUser.getId());
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            System.out.println("Registration error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body("Registration failed: " + e.getMessage());
        }
    }
}
