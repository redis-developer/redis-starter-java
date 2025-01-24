package io.redis.todoapp.components.todos;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/todos")
public class TodoController {
    private static final Logger logger = LoggerFactory.getLogger(TodoController.class);

    @Autowired
    private TodoRepository repository;

    public TodoController() {
    }

    @GetMapping("")
    public ResponseEntity<?> all() {
        logger.info("GET request access '/api/todos' path.");
        try {
            var todos = repository.all();
            return new ResponseEntity<>(todos, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(String.format("Error getting all todos %s", e), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> allTrailing() {
        return all();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> one(@PathVariable String id) {
        return new ResponseEntity<>(repository.one(id), HttpStatus.OK);
    }
}
