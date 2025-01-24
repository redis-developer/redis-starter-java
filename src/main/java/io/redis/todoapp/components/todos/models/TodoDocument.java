package io.redis.todoapp.components.todos.models;

public class TodoDocument {
    private final String id;
    private final Todo value;

    public TodoDocument(String id, Todo value) {
        this.id = id;
        this.value = value;
    }

    public String getId() {
        return id;
    }

    public Todo getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "TodoDocument [id=" + id + ", value=" + value.toString() + "]";
    }
}
