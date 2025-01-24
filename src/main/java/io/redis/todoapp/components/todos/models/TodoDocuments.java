package io.redis.todoapp.components.todos.models;

import java.util.List;

public class TodoDocuments {
    private final long total;
    private final List<TodoDocument> documents;

    public TodoDocuments(long total, List<TodoDocument> documents) {
        this.total = total;
        this.documents = documents;
    }

    public long getTotal() {
        return total;
    }

    public List<TodoDocument> getDocuments() {
        return documents;
    }

    @Override
    public String toString() {
        return "TodoDocuments [total=" + total + "]";
    }
}
