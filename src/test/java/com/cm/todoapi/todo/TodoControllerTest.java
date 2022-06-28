package com.cm.todoapi.todo;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TodoController.class)
@ExtendWith(SpringExtension.class)
class TodoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    TodoService todoService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldGetAllTodos() throws Exception {
        List<Todo> todoList = List.of(
                new Todo(1,"Clean Room","Arrange the cupboard and sweep the floor",false, LocalDate.now()),
                new Todo(2,"Watch Movie","Watch Thor L&T",false, LocalDate.now())
        );
        when(todoService.getAllTodos()).thenReturn(todoList);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/todo").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        List<Todo> actualTodos = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<List<Todo>>(){});
        assertEquals(todoList.toString(),actualTodos.toString());
    }

    @Test
    void shouldCreateANewTodo() throws Exception {
        Todo todo = new Todo(1,"Clean Room","Arrange the cupboard and sweep the floor",false, LocalDate.parse("2020-01-01"));
        when(todoService.createNewTodo(any())).thenReturn(todo);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/todo").contentType(MediaType.APPLICATION_JSON).content(
                        """
                        {
                            "title": "Clean Room",
                            "description": "Arrange the cupboard and sweep the floor",
                            "checked":false,
                            "createdAt": "2020-01-02"
                        }
                        """
                ))
                .andExpect(status().isOk())
                .andReturn();

        Todo actualTodo = objectMapper.readValue(result.getResponse().getContentAsString(), Todo.class);
        assertEquals(todo.toString(),actualTodo.toString());
    }
}
