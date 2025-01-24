package io.redis.todoapp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.redis.todoapp.components.todos.TodoRepository;

@Configuration
public class TodoRepositoryConfig {
    @Bean
    public TodoRepository getInstance() {
        return new TodoRepository();
    }
}