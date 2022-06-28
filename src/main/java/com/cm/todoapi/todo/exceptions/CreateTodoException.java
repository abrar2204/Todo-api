package com.cm.todoapi.todo.exceptions;

import java.util.List;

public class CreateTodoException extends RuntimeException {
    private final String message;
    private final List<String> errors;
    public CreateTodoException(String message, List<String> errors) {
        this.message=message;
        this.errors=errors;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }
}
