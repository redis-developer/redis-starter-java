package io.redis.todoapp.components.todos;

import java.lang.invoke.MethodHandles;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.redis.todoapp.components.todos.models.CreateTodoDto;
import io.redis.todoapp.components.todos.models.UpdateTodoDto;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private final static Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private TodoRepository repository;

    public TodoController() {
    }

    @GetMapping("")
    public ResponseEntity<?> all() {
        logger.info("GET /api/todos.");
        try {
            var todos = repository.all();
            return new ResponseEntity<>(todos, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(String.format("Error getting all todos %s", e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> allTrailing() {
        return all();
    }

    @GetMapping("/search")
    public ResponseEntity<?> search(@RequestParam(required = false) String name, @RequestParam(required = false) String status) {
        logger.info("GET /api/todos/search");
        try {
            var todos = repository.search(name, status);
            return new ResponseEntity<>(todos, HttpStatus.OK);
        } catch (Exception e) {
            logger.error(e.getMessage());
            return new ResponseEntity<>(String.format("Error getting all todos %s", e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/search/")
    public ResponseEntity<?> searchTrailing(@RequestParam(required = false) String name, @RequestParam(required = false) String status) {
        return search(name, status);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable String id) {
        logger.info(String.format("GET /api/todos/%s", id));
        return new ResponseEntity<>(repository.one(id), HttpStatus.OK);
    }

    @GetMapping("/{id}/")
    public ResponseEntity<?> oneTrailing(@PathVariable String id) {
        return one(id);
    }

    @PostMapping("")
    public ResponseEntity<?> create(@RequestBody CreateTodoDto todo) {
        logger.info(String.format("POST /api/todos/ (%s)", todo));
        return new ResponseEntity<>(repository.create(todo), HttpStatus.OK);
    }

    @PostMapping("/")
    public ResponseEntity<?> createTrailing(@RequestBody CreateTodoDto todo) {
        return create(todo);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody UpdateTodoDto todo) {
        logger.info(String.format("PATCH /api/todos/%s (%s)", id, todo));
        return new ResponseEntity<>(repository.update(id, todo), HttpStatus.OK);
    }

    @PatchMapping("/{id}/")
    public ResponseEntity<?> updateTrailing(@PathVariable String id, @RequestBody UpdateTodoDto todo) {
        return update(id, todo);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable String id) {
        repository.delete(id);
    }

    @DeleteMapping("/{id}/")
    public void deleteTrailing(@PathVariable String id) {
        delete(id);
    }
}
