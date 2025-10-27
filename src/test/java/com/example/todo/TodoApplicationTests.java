package com.example.todo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.jpa.hibernate.ddl-auto=create-drop",
    "spring.task.scheduling.enabled=false",
    "spring.profiles.active=test"
})
class TodoApplicationTests {

    @Test
    void contextLoads() {
        // This test will pass if the application context loads successfully
    }
}