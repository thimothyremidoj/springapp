package com.example.todo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = FutureDateTimeValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface FutureDateTime {
    String message() default "Due date must be in the future";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    
    int minutesFromNow() default 1;
}