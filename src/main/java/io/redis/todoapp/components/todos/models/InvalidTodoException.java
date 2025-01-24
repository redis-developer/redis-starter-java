package io.redis.todoapp.components.todos.models;

public class InvalidTodoException extends Exception {
    public InvalidTodoException(String message) {
        super(message);
    }
}
