package com.example.todo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class FutureDateTimeValidator implements ConstraintValidator<FutureDateTime, LocalDateTime> {
    
    private int minutesFromNow;
    
    @Override
    public void initialize(FutureDateTime constraintAnnotation) {
        this.minutesFromNow = constraintAnnotation.minutesFromNow();
    }
    
    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }
        
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.plusMinutes(minutesFromNow);
        
        return value.isAfter(threshold);
    }
}