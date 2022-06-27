package com.cm.todoapi.todo;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class TodoServiceTest {

    @Autowired
    TodoService todoService;

    @Autowired
    TodoRepository todoRepository;

    @BeforeEach()
    void setUp(){
        List<Todo> todoList = List.of(
                new Todo(null,"Clean Room","Arrange the cupboard and sweep the floor",false, LocalDate.now()),
                new Todo(null,"Watch Movie","Watch Thor L&T",false, LocalDate.now())
        );
        todoRepository.saveAll(todoList);
    }

    @Test
    void shouldGetAllMovies() {
        List<Todo> todoList = List.of(
                new Todo(1,"Clean Room","Arrange the cupboard and sweep the floor",false, LocalDate.now()),
                new Todo(2,"Watch Movie","Watch Thor L&T",false, LocalDate.now())
        );

        List<Todo> actualTodos = todoService.getAllTodos();

        assertEquals(todoList.toString(),actualTodos.toString());
    }

    @AfterEach
    void tearDown() {
        todoRepository.deleteAll();
    }
}
