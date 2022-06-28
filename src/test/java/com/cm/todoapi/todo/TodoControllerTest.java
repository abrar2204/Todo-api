package com.cm.todoapi.todo;

import com.cm.todoapi.todo.response.TodoResponse;
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

    final String TODO_RESPONSE_ALL_TODOS = """
            {
                "success": [
                    {
                        "id": 1,
                        "title": "Clean Room",
                        "description": "Arrange the cupboard and sweep the floor",
                        "checked": false,
                        "createdAt": "2020-01-01"
                    },
                    {
                        "id": 2,
                        "title": "Watch Movie",
                        "description": "Watch Thor L&T",
                        "checked": false,
                        "createdAt": "2020-01-01"
                    }
                ],
                "error": null
            }
            """;
    final String TODO_REQUEST_WITH_ALL_FIELDS = """
            {
                "title": "Clean Room",
                "description": "Arrange the cupboard and sweep the floor",
                "checked": false,
                "createdAt": "2020-01-01"
            }
            """;
    final String TODO_RESPONSE_WITH_ALL_FIELDS = """
            {
                "success": {
                    "id": 1,
                    "title": "Clean Room",
                    "description": "Arrange the cupboard and sweep the floor",
                    "checked": false,
                    "createdAt": "2020-01-01"
                },
                "error": null
            }
            """;
    final String TODO_REQUEST_WITH_MISSING_FIELDS = """
            {
                "title": "Watch Movie",
                "description": "Arrange the cupboard and sweep the floor",
                "checked": false
            }
            """;
    final String TODO_RESPONSE_FOR_MISSING_FIELDS = """
            {
                "success": null,
                "error": [
                    "Provide a created date"
                ]
            }
            """;

    @Test
    void shouldGetAllTodos() throws Exception {
        List<Todo> todoList = List.of(
                new Todo(1, "Clean Room", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01")),
                new Todo(2, "Watch Movie", "Watch Thor L&T", false, LocalDate.parse("2020-01-01"))
        );
        when(todoService.getAllTodos()).thenReturn(todoList);
        String expectedResponse = objectMapper.readValue(TODO_RESPONSE_ALL_TODOS,TodoResponse.class).toString();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = objectMapper.readValue(result.getResponse().getContentAsString(),TodoResponse.class).toString();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldCreateANewTodo() throws Exception {
        Todo todo = new Todo(1, "Clean Room", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01"));
        when(todoService.createNewTodo(any())).thenReturn(todo);
        String expectedResponse = objectMapper.readValue(TODO_RESPONSE_WITH_ALL_FIELDS, TodoResponse.class).toString();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TODO_REQUEST_WITH_ALL_FIELDS))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TodoResponse.class).toString();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldNotCreateNewTodoWhenRequiredFieldsAreNotGiven() throws Exception {
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TODO_REQUEST_WITH_MISSING_FIELDS))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        assertEquals(objectMapper.readValue(TODO_RESPONSE_FOR_MISSING_FIELDS, TodoResponse.class).toString(), objectMapper.readValue(result.getResponse().getContentAsString(), TodoResponse.class).toString());
    }
}
