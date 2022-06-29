package com.cm.todoapi.todo.service;

import com.cm.todoapi.todo.exceptions.TodoNotFoundException;
import com.cm.todoapi.todo.model.Todo;
import com.cm.todoapi.todo.repository.TodoRepository;
import com.cm.todoapi.todo.service.TodoService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TodoServiceTest {
    @Autowired
    TodoService todoService;

    @Autowired
    TodoRepository todoRepository;

    @BeforeEach()
    void setUp() {
        List<Todo> todoList = List.of(
                new Todo(null, "Clean Room", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01")),
                new Todo(null, "Watch Movie", "Watch Thor L&T", false, LocalDate.parse("2020-01-02"))
        );
        todoRepository.saveAll(todoList);
    }

    @Test
    void shouldGetAllTodo() {
        List<Todo> todoList = List.of(
                new Todo(1, "Clean Room", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01")),
                new Todo(2, "Watch Movie", "Watch Thor L&T", false, LocalDate.parse("2020-01-02"))
        );

        List<Todo> actualTodos = todoService.getAllTodos();

        assertEquals(todoList, actualTodos);
    }

    @Test
    void shouldCreateNewTodo() {
        Todo newTodo = new Todo(1, "Clean Room", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01"));

        Todo actualTodo = todoService.createNewTodo(newTodo);

        assertEquals(newTodo, actualTodo);
    }

    @Test
    void shouldDeleteATodo() {
        Todo remainingTodo = new Todo(2, "Watch Movie", "Watch Thor L&T", false, LocalDate.parse("2020-01-02"));

        todoService.deleteTodoById(1);

        assertEquals(1, todoRepository.findAll().size());
        assertEquals(remainingTodo, todoRepository.findAll().get(0));
    }

    @Test
    void shouldThrowExceptionWhenTodoThatDoesNotExistIsDeleted() {
        assertThrows(TodoNotFoundException.class, () -> todoService.deleteTodoById(10));
    }

    @Test
    void shouldUpdateATodoById() {
        Todo todo = new Todo(1, "Clean Room 2", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01"));

        Todo updatedTodo = todoService.updateTodoById(1,todo);

        assertEquals(todo,updatedTodo);
    }

    @Test
    void shouldThrowExceptionWhenTodoThatDoesNotExistIsUpdated() {
        assertThrows(TodoNotFoundException.class, () -> todoService.updateTodoById(10,new Todo()));
    }

    @AfterEach
    void tearDown() {
        todoRepository.deleteAll();
    }
}
