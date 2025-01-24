package io.redis.todoapp.components.todos.models;

import java.util.UUID;

import com.google.gson.annotations.SerializedName;

public class CreateTodoDto {
    @SerializedName("id")
    private String id;

    @SerializedName("name")
    private String name;

    public CreateTodoDto() {
        this.id = UUID.randomUUID().toString();
    }

    public CreateTodoDto(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }

    public CreateTodoDto(String id, String name) {
        this.name = name;
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public Todo toTodo() {
        return new Todo(name);
    }

    public TodoDocument toTodoDocument() {
        return new TodoDocument(id, toTodo());
    }

    @Override
    public String toString() {
        return String.format("Todo [id=%s, name=%s]", id, name);
    }
}
