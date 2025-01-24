/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	  https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.redis.todoapp;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import io.redis.todoapp.components.todos.TodoRepository;
import io.redis.todoapp.components.todos.models.CreateTodoDto;
import io.redis.todoapp.components.todos.models.UpdateTodoDto;

@SpringBootTest
@TestInstance(Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {TodoRepository.class, GsonConfiguration.class, JedisConfig.class}, loader = AnnotationConfigContextLoader.class)
public class TodoRepositoryTests {

    @Autowired
    private TodoRepository repository;

    @BeforeAll
    public void beforeAll() {
        repository.dropIndex();
        repository.createIndexIfNotExists();
    }

    @BeforeEach
    public void beforeEach() {
        repository.deleteAll();
    }

    @AfterAll
    public void afterAll() {
        repository.deleteAll();
        repository.dropIndex();
    }

    @Test
    public void crudForASingleTodo() throws Exception {
        var sampleTodo = new CreateTodoDto("Take out the trash");
        var createdTodo = repository.create(sampleTodo);

        assertNotNull(createdTodo);
        assertNotNull(createdTodo.getValue());
        var name = createdTodo.getValue().getName();
        var id = createdTodo.getId();
        assertEquals(sampleTodo.getName(), name);
        assertEquals(sampleTodo.getId(), id);

        var readResult = repository.one(id);

        assertNotNull(readResult);
        assertEquals(name, readResult.getName());

        var update = new UpdateTodoDto("in progress");
        var updateResult = repository.update(id, update);

        assertNotNull(updateResult);
        assertEquals(name, updateResult.getName());
        assertEquals(update.getStatus(), updateResult.getStatus());
        assertNotNull(updateResult.getCreatedDate());
        assertNotNull(updateResult.getUpdatedDate());
        assertNotNull(createdTodo.getValue().getUpdatedDate());
        assertTrue(createdTodo.getValue().getCreatedDate().compareTo(updateResult.getCreatedDate()) == 0);
        assertTrue(createdTodo.getValue().getCreatedDate().isBefore(updateResult.getUpdatedDate()));

        repository.delete(id);
    }

	@Test
	public void createAndReadMultipleTodos() {
		var todos = List.of(
			"Take out the trash",
            "Vacuum downstairs",
            "Fold the laundry"
		);

		for (var todo: todos) {
			assertDoesNotThrow(() -> repository.create(new CreateTodoDto(todo)));
		}

		var allTodos = repository.all();

		assertNotNull(allTodos);
		assertNotNull(allTodos.getDocuments());
		assertEquals(todos.size(), allTodos.getTotal());
		assertEquals(todos.size(), allTodos.getDocuments().size());

		for (var todo: allTodos.getDocuments()) {
			assertNotNull(todo);
			assertNotNull(todo.getValue());
			assertTrue(todos.contains(todo.getValue().getName()));
		}
	}
}
