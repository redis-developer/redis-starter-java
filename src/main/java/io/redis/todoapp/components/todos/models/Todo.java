package io.redis.todoapp.components.todos.models;

import java.time.Instant;

import com.google.gson.annotations.SerializedName;

public class Todo {
    @SerializedName("name")
    private String name;

    @SerializedName("status")
    private String status;

    @SerializedName("created_date")
    private Instant createdDate;

    @SerializedName("updated_date")
    private Instant updatedDate;

    public Todo() {
        this.status = "todo";
        this.createdDate = Instant.now();
        this.updatedDate = this.createdDate;
    }

    public Todo(String name) {
        this.name = name;
        this.status = "todo";
        this.createdDate = Instant.now();
        this.updatedDate = this.createdDate;
    }

    public Todo(String name, String status) {
        this.name = name;
        this.status = status;
        this.createdDate = Instant.now();
        this.updatedDate = this.createdDate;
    }

    public Todo(String name, String status, Instant createdDate, Instant updatedDate) {
        this.name = name;
        this.status = status;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
    }

    public Todo(String name, String status, String createdDate, String updatedDate) {
        this.name = name;
        this.status = status;
        this.createdDate = Instant.parse(createdDate);
        this.updatedDate = Instant.parse(updatedDate);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setCreatedDate(Instant date) {
        this.createdDate = date;
    }

    public Instant getCreatedDate() {
        return createdDate;
    }

    public void setUpdatedDate(Instant date) {
        this.updatedDate = date;
    }

    public Instant getUpdatedDate() {
        return updatedDate;
    }

    @Override
    public String toString() {
        return String.format("Todo [name=%s, status=%s, created=%s, updated=%s]", name, status, createdDate, updatedDate);
    }
}
