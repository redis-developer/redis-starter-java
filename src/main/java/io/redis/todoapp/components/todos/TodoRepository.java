package io.redis.todoapp.components.todos;

import java.lang.invoke.MethodHandles;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.gson.Gson;

import io.redis.todoapp.components.todos.models.CreateTodoDto;
import io.redis.todoapp.components.todos.models.InvalidTodoException;
import io.redis.todoapp.components.todos.models.Todo;
import io.redis.todoapp.components.todos.models.TodoDocument;
import io.redis.todoapp.components.todos.models.TodoDocuments;
import io.redis.todoapp.components.todos.models.TodoNotFoundException;
import io.redis.todoapp.components.todos.models.UpdateTodoDto;
import jakarta.annotation.PostConstruct;
import redis.clients.jedis.UnifiedJedis;
import redis.clients.jedis.search.FTCreateParams;
import redis.clients.jedis.search.IndexDataType;
import redis.clients.jedis.search.SearchResult;
import redis.clients.jedis.search.schemafields.SchemaField;
import redis.clients.jedis.search.schemafields.TextField;

@Repository
public class TodoRepository {
    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static final String TODOS_INDEX = "todos-idx";
    public static final String TODOS_PREFIX = "todos:";

    @Autowired
    private Gson gson;

    @Autowired
    private UnifiedJedis redis;

    @PostConstruct
    public void init() {
        this.createIndexIfNotExists();
    }

    private String formatId(String id) {
        if (id.matches("^" + TODOS_PREFIX + ".*")) {
            return id;
        }

        return TODOS_PREFIX + id;
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

    public void dropIndex() {
        if (!haveIndex()) {
            return;
        }

        redis.ftDropIndex(TODOS_INDEX);
    }

    private TodoDocuments toTodoDocuments(SearchResult result) {
        var total = result.getTotalResults();
        var documents = result.getDocuments();
        List<TodoDocument> todos = new ArrayList<>();

        logger.debug("found " + documents.size() + " documents");
        for (var doc : documents) {
            var todo = jsonToTodo(doc.get("$"));
            todos.add(new TodoDocument(doc.getId(), todo));
        }

        var todoDocuments = new TodoDocuments(total, todos);

        logger.debug(String.format("\n%s\n", todoDocuments.toString()));

        return todoDocuments;
    }

    public TodoDocuments all() {
        var result = redis.ftSearch(TODOS_INDEX, "*");
        return toTodoDocuments(result);
    }

    public TodoDocuments search(String name, String status) {
        List<String> searches = new ArrayList<>();

        if (name != null) {
            searches.add(String.format("@name:(%s)", name));
        }

        if (status != null) {
            searches.add(String.format("@status:%s", status));
        }

        var result = redis.ftSearch(TODOS_INDEX, String.join(" ", searches));

        return toTodoDocuments(result);
    }

    public TodoDocument create(CreateTodoDto todoDto) throws InvalidTodoException {
        todoDto.setId(formatId(todoDto.getId()));
        var todoDocument = todoDto.toTodoDocument();
        var ok = redis.jsonSet(todoDocument.getId(), gson.toJson(todoDocument.getValue()));

        logger.debug(String.format("jsonSet(%s, %s) == %s", todoDocument.getId(), todoDocument.getValue(), ok));

        if (ok.toLowerCase().equals("ok")) {
            logger.debug(String.format("Todo created: %s", todoDocument));
            return todoDocument;
        }

        throw new InvalidTodoException("failed to create todo");
    }

    public Todo one(String id) throws TodoNotFoundException {
        var result = redis.jsonGet(formatId(id));

        if (result == null) {
            throw new TodoNotFoundException(id);
        }

        return jsonToTodo(result);
    }

    public Todo update(String id, UpdateTodoDto todoDto) throws TodoNotFoundException, InvalidTodoException {
        var status = todoDto.getStatus();
        switch (status) {
            case "todo", "in progress", "complete" -> {
            }
            default -> throw new InvalidTodoException(String.format("invalid status \"%s\"", status));
        }

        id = formatId(id);
        var todo = one(id);
    
        if (todo == null) {
            throw new TodoNotFoundException(id);
        }

        todo.setStatus(status);
        todo.setUpdatedDate(Instant.now());
        var ok = redis.jsonSet(id, gson.toJson(todo));
    
        if (ok.toLowerCase().equals("ok")) {
            logger.debug(String.format("Todo updated: %s", todo));
            return todo;
        }

        throw new InvalidTodoException(String.format("failed to update todo \"%s\"", id));
    }

    public void delete(String id) {
        redis.jsonDel(formatId(id));
    }

    public void deleteAll() {
        var todos = all();

        if (todos.getTotal() <= 0) {
            return;
        }

        List<String> ids = new ArrayList<>();
        
        for (var doc: todos.getDocuments()) {
            ids.add(doc.getId());
        }

        redis.del(ids.toArray(String[]::new));
    }

    private Todo jsonToTodo(Object obj) {
        if (obj instanceof String string) {
            return gson.fromJson(string, Todo.class);
        }

        var json = gson.toJson(obj);
        return gson.fromJson(json, Todo.class);
    }
}
