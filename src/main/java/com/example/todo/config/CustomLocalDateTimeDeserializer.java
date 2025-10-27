package com.example.todo.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CustomLocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {
    
    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateString = p.getValueAsString();
        
        if (dateString == null || dateString.isEmpty()) {
            return null;
        }
        
        // Remove Z suffix if present
        if (dateString.endsWith("Z")) {
            dateString = dateString.substring(0, dateString.length() - 1);
        }
        
        try {
            // Try parsing with LocalDateTime.parse() which handles multiple formats
            return LocalDateTime.parse(dateString);
        } catch (Exception e) {
            // If that fails, try manual parsing for HH:mm format
            if (dateString.matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}")) {
                return LocalDateTime.parse(dateString + ":00");
            }
            throw new IOException("Unable to parse date: " + dateString, e);
        }
    }
}