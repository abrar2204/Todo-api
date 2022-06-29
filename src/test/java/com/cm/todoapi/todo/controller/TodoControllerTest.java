package com.cm.todoapi.todo.controller;

import com.cm.todoapi.todo.controller.TodoController;
import com.cm.todoapi.todo.exceptions.TodoNotFoundException;
import com.cm.todoapi.todo.model.Todo;
import com.cm.todoapi.todo.response.TodoResponse;
import com.cm.todoapi.todo.service.TodoService;
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
import static org.mockito.Mockito.*;
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
                        "completed": false,
                        "createdAt": "2020-01-01"
                    },
                    {
                        "id": 2,
                        "title": "Watch Movie",
                        "description": "Watch Thor L&T",
                        "completed": false,
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
                "completed": false,
                "createdAt": "2020-01-01"
            }
            """;
    final String TODO_RESPONSE_WITH_ALL_FIELDS = """
            {
                "success": {
                    "id": 1,
                    "title": "Clean Room",
                    "description": "Arrange the cupboard and sweep the floor",
                    "completed": false,
                    "createdAt": "2020-01-01"
                },
                "error": null
            }
            """;
    final String TODO_REQUEST_WITH_MISSING_FIELDS = """
            {
                "title": "Watch Movie",
                "description": "Arrange the cupboard and sweep the floor",
                "completed": false
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

    final String TODO_DELETE_SUCCESS_RESPONSE = """
            {
                "success": "Successfully deleted 1 todo",
                "error": null
            }
            """;

    final String TODO_DELETE_FAILURE_RESPONSE = """
            {
                "success": null,
                "error": "Todo with id 1 is not found"
            }
            """;

    final String TODO_UPDATE_BODY = """
                {
                    "id": 1,
                    "title": "Clean Room",
                    "description": "Arrange the cupboard and sweep the floor",
                    "completed": false,
                    "createdAt": "2020-01-01"
                }
                """;

    final String TODO_UPDATE_SUCCESS_RESPONSE = """
                    {
                        "success":{
                            "id": 1,
                            "title": "Clean Room",
                            "description": "Arrange the cupboard and sweep the floor",
                            "completed": false,
                            "createdAt": "2020-01-01"
                        },
                        "error": null
                    }
                """;

    final String TODO_UPDATE_FAILURE_RESPONSE = """
            {
                "success": null,
                "error": "Todo with id 10 is not found"
            }
            """;

    @Test
    void shouldGetAllTodos() throws Exception {
        List<Todo> todoList = List.of(
                new Todo(1, "Clean Room", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01")),
                new Todo(2, "Watch Movie", "Watch Thor L&T", false, LocalDate.parse("2020-01-01"))
        );
        when(todoService.getAllTodos()).thenReturn(todoList);
        String expectedResponse = getResponseStringFromJSONString(TODO_RESPONSE_ALL_TODOS);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = getResponseStringFromMvcResult(result);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldCreateANewTodo() throws Exception {
        Todo passedTodo = new Todo(null, "Clean Room", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01"));
        Todo returnedTodo = new Todo(1, "Clean Room", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01"));
        when(todoService.createNewTodo(passedTodo)).thenReturn(returnedTodo);
        String expectedResponse = getResponseStringFromJSONString(TODO_RESPONSE_WITH_ALL_FIELDS);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TODO_REQUEST_WITH_ALL_FIELDS))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = getResponseStringFromMvcResult(result);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldNotCreateNewTodoWhenRequiredFieldsAreNotGiven() throws Exception {
        String expectedResponse = getResponseStringFromJSONString(TODO_RESPONSE_FOR_MISSING_FIELDS);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TODO_REQUEST_WITH_MISSING_FIELDS))
                .andExpect(status().isUnprocessableEntity())
                .andReturn();

        String actualResponse = getResponseStringFromMvcResult(result);
        assertEquals(expectedResponse,actualResponse);
    }

    @Test
    void shouldDeleteATodo() throws Exception {
        String expectedResponse = getResponseStringFromJSONString(TODO_DELETE_SUCCESS_RESPONSE);
        doNothing().when(todoService).deleteTodoById(1);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/todo/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = getResponseStringFromMvcResult(result);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldThrowErrorWhenTodoThatDoesNotExistIsDeleted() throws Exception {
        String expectedResponse = getResponseStringFromJSONString(TODO_DELETE_FAILURE_RESPONSE);
        doThrow(new TodoNotFoundException("Todo with id 1 is not found")).when(todoService).deleteTodoById(1);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/todo/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = getResponseStringFromMvcResult(result);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldUpdateATodoBasedOnId() throws Exception {
        String expectedResponse = getResponseStringFromJSONString(TODO_UPDATE_SUCCESS_RESPONSE);
        Todo todo = new Todo(1, "Clean Room", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01"));
        when(todoService.updateTodoById(1,todo)).thenReturn(todo);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/todo/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TODO_UPDATE_BODY)
                )
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = getResponseStringFromMvcResult(result);
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldThrowExceptionWhenTodoThatDoesNotExistIsUpdated() throws Exception {
        Todo passedTodo = new Todo(1, "Clean Room", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01"));
        String expectedResponse = getResponseStringFromJSONString(TODO_UPDATE_FAILURE_RESPONSE);
        when(todoService.updateTodoById(10,passedTodo)).thenThrow(new TodoNotFoundException(10));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/todo/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(TODO_UPDATE_BODY)
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = getResponseStringFromMvcResult(result);
        assertEquals(expectedResponse,actualResponse);
    }

    String getResponseStringFromMvcResult(MvcResult result) throws Exception {
        return objectMapper.readValue(result.getResponse().getContentAsString(), TodoResponse.class).toString();
    }

    String getResponseStringFromJSONString(String jsonString) throws Exception {
        return objectMapper.readValue(jsonString, TodoResponse.class).toString();
    }
}
