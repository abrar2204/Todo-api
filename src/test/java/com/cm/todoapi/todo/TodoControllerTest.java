package com.cm.todoapi.todo;

import com.cm.todoapi.todo.exceptions.TodoNotFoundException;
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

import java.io.UnsupportedEncodingException;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
        String expectedResponse = objectMapper.readValue(TODO_RESPONSE_ALL_TODOS, TodoResponse.class).toString();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/todo")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TodoResponse.class).toString();
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

    @Test
    void shouldDeleteATodo() throws Exception {
        String expectedResponse = objectMapper.readValue(TODO_DELETE_SUCCESS_RESPONSE, TodoResponse.class).toString();
        doNothing().when(todoService).deleteTodoById(1);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/todo/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = objectMapper.readValue(result.getResponse().getContentAsString(), TodoResponse.class).toString();
        assertEquals(expectedResponse, actualResponse);
    }

    @Test
    void shouldThrowErrorWhenTodoThatDoesNotExistIsDeleted() throws Exception {
        String expectedResponse = objectMapper.readValue(TODO_DELETE_FAILURE_RESPONSE, TodoResponse.class).toString();
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
        String expectedResponse = objectMapper.readValue(TODO_UPDATE_SUCCESS_RESPONSE, TodoResponse.class).toString();
        Todo todo = new Todo(1, "Clean Room", "Arrange the cupboard and sweep the floor", false, LocalDate.parse("2020-01-01"));
        when(todoService.updateTodoById(any(),any())).thenReturn(todo);

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
        String expectedResponse = objectMapper.readValue(TODO_UPDATE_FAILURE_RESPONSE,TodoResponse.class).toString();
        when(todoService.updateTodoById(any(),any())).thenThrow(new TodoNotFoundException(10));

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
}
