package com.cm.todoapi.todo.exceptions;


public class TodoNotFoundException extends RuntimeException {
    String message;
    public TodoNotFoundException(String message) {
        super(message);
        this.message = message;
    }
}
