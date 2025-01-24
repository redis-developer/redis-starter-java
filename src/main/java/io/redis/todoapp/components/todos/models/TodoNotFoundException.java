package io.redis.todoapp.components.todos.models;

public class TodoNotFoundException extends Exception {
    public TodoNotFoundException(String id) {
        super(String.format("Todo \"%s\" not found", id));
    }
}
