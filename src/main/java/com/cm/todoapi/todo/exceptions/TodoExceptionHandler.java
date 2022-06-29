package com.cm.todoapi.todo.exceptions;

import com.cm.todoapi.todo.exceptions.TodoNotFoundException;
import com.cm.todoapi.todo.response.TodoResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class TodoExceptionHandler {

    @ExceptionHandler(TodoNotFoundException.class)
    ResponseEntity<TodoResponse> handleTodoNotFoundException(TodoNotFoundException exception){
        return ResponseEntity.badRequest().body(new TodoResponse(null,exception.getMessage()));
    }

}
