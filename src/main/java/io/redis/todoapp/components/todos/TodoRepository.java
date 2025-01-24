package io.redis.todoapp.components.todos;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.gson.GsonBuilderCustomizer;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.redis.todoapp.components.todos.models.Todo;
import io.redis.todoapp.components.todos.models.TodoDocument;
import io.redis.todoapp.components.todos.models.TodoDocuments;
import jakarta.annotation.PostConstruct;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.IndexDataType;
import redis.clients.jedis.search.schemafields.SchemaField;
import redis.clients.jedis.search.schemafields.TextField;

@Repository
public class TodoRepository {

    public static final String TODOS_INDEX = "todos-idx";
    public static final String TODOS_PREFIX = "todos:";

    @Autowired
    private GsonBuilderCustomizer gsonCustomizer;

    private Gson gson;

    @Autowired
    private UnifiedJedis redis;

    @PostConstruct
    public void init() {
        var gsonBuilder = new GsonBuilder();
        gsonCustomizer.customize(gsonBuilder);
        gson = gsonBuilder.create();

        this.createIndexIfNotExists();
    }

    private boolean haveIndex() {
        var indexes = redis.ftList();

        return indexes.contains(TODOS_INDEX);
    }

    public void createIndexIfNotExists() {
        if (haveIndex()) {
            return;
        }

        SchemaField[] schema = {
            TextField.of("$.name").as("name"),
            TextField.of("$.status").as("status")
        };
        redis.ftCreate(TODOS_INDEX,
                FTCreateParams.createParams()
                        .on(IndexDataType.JSON)
                        .addPrefix(TODOS_PREFIX),
                schema
        );
    }

    public TodoDocuments all() {
        var result = redis.ftSearch(TODOS_INDEX, "*");
        var total = result.getTotalResults();
        var documents = result.getDocuments();
        List<TodoDocument> todos = new ArrayList<>();

        for (var doc : documents) {
            var name = doc.getString("name");
            var status = doc.getString("status");
            var createdDate = doc.getString("created_date");
            var updatedDate = doc.getString("updated_date");
            var todo = new Todo(name, status, createdDate, updatedDate);
            todos.add(new TodoDocument(doc.getId(), todo));
        }

        return new TodoDocuments(total, todos);
    }

    public Todo one(String id) {
        var result = redis.jsonGet(id);

        return jsonToTodo(result);
    }

    private Todo jsonToTodo(Object obj) {
        var json = gson.toJson(obj);
        return gson.fromJson(json, Todo.class);
    }
}
