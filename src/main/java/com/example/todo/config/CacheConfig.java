package com.example.todo.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Simple caching configuration
    // Tasks and users will be cached automatically with @Cacheable annotations
}