package io.redis.todoapp.components.todos.models;

import com.google.gson.annotations.SerializedName;

public class UpdateTodoDto {
    @SerializedName("status")
    private String status;

    public UpdateTodoDto() {
    }

    public UpdateTodoDto(String status) {
        this.status = status.toLowerCase();
    }

    public void setStatus(String status) {
        this.status = status.toLowerCase();
    }

    public String getStatus() {
        return status.toLowerCase();
    }

    @Override
    public String toString() {
        return String.format("UpdateTodoDto [status=%s]", status);
    }
}
