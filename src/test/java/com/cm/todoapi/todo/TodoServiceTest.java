package com.cm.todoapi.todo;

import com.cm.todoapi.todo.exceptions.TodoNotFoundException;
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
@Transactional
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class TodoServiceTest {

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

        assertEquals(todoList.toString(), actualTodos.toString());
    }

    @Test
    void shouldCreateNewTodo() {
        Todo newTodo = new Todo(1, "Clean Room", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01"));

        Todo actualTodo = todoService.createNewTodo(newTodo);

        assertEquals(newTodo.toString(), actualTodo.toString());
    }

    @Test
    void shouldDeleteATodo() {
        todoService.deleteTodoById(1);

        assertEquals(1, todoRepository.findAll().size());
        assertEquals(new Todo(null, "Watch Movie", "Watch Thor L&T", false, LocalDate.parse("2020-01-02")).toString(), todoRepository.findAll().get(0).toString());
    }

    @Test
    void shouldThrowExceptionWhenTodoThatDoesNotExistIsDeleted() {
        assertThrows(TodoNotFoundException.class, () -> todoService.deleteTodoById(10));
    }

    @AfterEach
    void tearDown() {
        todoRepository.deleteAll();
    }
}
