package com.cm.todoapi.todo.integration;

import com.cm.todoapi.todo.model.Todo;
import com.cm.todoapi.todo.repository.TodoRepository;
import com.cm.todoapi.todo.response.TodoResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TodoIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private TodoRepository todoRepository;
    @Autowired
    private ObjectMapper objectMapper;

    final String BASE_DIR_JSON = "src/test/resources/integration/";

    final String CREATE_TODO_SUCCESS_BODY = """
                {
                    "title": "Get Pizza",
                    "description": "Order Pizza from Dominos",
                    "completed": false,
                    "createdAt": "2020-01-03"
                }
                """;


    final String CREATE_TODO_FAILURE_BODY = """
                {
                    "description": "Order Pizza from Dominos",
                    "completed": false,
                    "createdAt": "2020-01-03"
                }
                """;

    final String UPDATE_TODO_BODY = """
                {
                    "title": "Clean Room",
                    "description": "Organize and sweep the room",
                    "completed": true,
                    "createdAt": "2022-06-27"
                }
                """;

    @Test
    void shouldSuccessfullyGetAllTodos() throws Exception {
        TodoResponse expectedResponse = getFromJsonFile("GetTodoSuccessResponse.json", TodoResponse.class);
        addTodosForTesting();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/api/todo").contentType(MediaType.APPLICATION_JSON)).andReturn();

        assertEquals(200,result.getResponse().getStatus());
        TodoResponse actualResponse = getFromJsonString(result.getResponse().getContentAsString(),TodoResponse.class);
        assertEquals(expectedResponse,actualResponse);
    }

    @Test
    void shouldSuccessfullyCreateNewTodo() throws Exception {
        TodoResponse expectedResponse = getFromJsonFile("CreateTodoSuccessResponse.json", TodoResponse.class);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(CREATE_TODO_SUCCESS_BODY)
        ).andReturn();

        assertEquals(200,result.getResponse().getStatus());
        TodoResponse actualResponse = getFromJsonString(result.getResponse().getContentAsString(),TodoResponse.class);
        assertEquals(expectedResponse,actualResponse);
    }

    @Test
    void shouldReturnErrorWhenInvalidDetailsAreGivenToCreateNewTodo() throws Exception {
        TodoResponse expectedResponse = getFromJsonFile("CreateTodoFailureResponse.json", TodoResponse.class);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content(CREATE_TODO_FAILURE_BODY)
        ).andReturn();

        assertEquals(422,result.getResponse().getStatus());
        TodoResponse actualResponse = getFromJsonString(result.getResponse().getContentAsString(),TodoResponse.class);
        assertEquals(expectedResponse,actualResponse);
    }

    @Test
    void shouldSuccessfullyUpdateATodo() throws Exception{
        TodoResponse expectedResponse = getFromJsonFile("UpdateTodoSuccessResponse.json", TodoResponse.class);
        addTodosForTesting();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/todo/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UPDATE_TODO_BODY)
        ).andReturn();

        assertEquals(200,result.getResponse().getStatus());
        TodoResponse actualResponse = getFromJsonString(result.getResponse().getContentAsString(),TodoResponse.class);
        assertEquals(expectedResponse,actualResponse);
    }

    @Test
    void shouldReturnErrorWhenTodoThatDoesNotExistIsUpdated() throws Exception{
        TodoResponse expectedResponse = getFromJsonFile("UpdateOrDeleteTodoFailureResponse.json", TodoResponse.class);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put("/api/todo/100")
                .contentType(MediaType.APPLICATION_JSON)
                .content(UPDATE_TODO_BODY)
        ).andReturn();

        assertEquals(400,result.getResponse().getStatus());
        TodoResponse actualResponse = getFromJsonString(result.getResponse().getContentAsString(),TodoResponse.class);
        assertEquals(expectedResponse,actualResponse);
    }

    @Test
    void shouldSuccessfullyDeleteTodo() throws Exception{
        TodoResponse expectedResponse = getFromJsonFile("DeleteTodoSuccessResponse.json", TodoResponse.class);
        addTodosForTesting();

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/todo/1")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(200,result.getResponse().getStatus());
        TodoResponse actualResponse = getFromJsonString(result.getResponse().getContentAsString(),TodoResponse.class);
        assertEquals(expectedResponse,actualResponse);
    }

    @Test
    void shouldReturnErrorWhenTodoThatDoesNotExistIsDeleted() throws Exception{
        TodoResponse expectedResponse = getFromJsonFile("UpdateOrDeleteTodoFailureResponse.json", TodoResponse.class);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.delete("/api/todo/100")
                .contentType(MediaType.APPLICATION_JSON)
        ).andReturn();

        assertEquals(400,result.getResponse().getStatus());
        TodoResponse actualResponse = getFromJsonString(result.getResponse().getContentAsString(),TodoResponse.class);
        assertEquals(expectedResponse,actualResponse);
    }

    void addTodosForTesting() throws Exception {
        List<Todo> todos = Arrays.asList(getFromJsonFile("Todos.json",Todo[].class));
        todoRepository.saveAll(todos);
    }

    protected <T> T getFromJsonFile(String fileName,Class<T> type) throws Exception {
        return objectMapper.readValue(Paths.get(BASE_DIR_JSON+fileName).toFile(),type);
    }

    protected <T> T getFromJsonString(String jsonString,Class<T> type) throws Exception{
        return objectMapper.readValue(jsonString,type);
    }
}
