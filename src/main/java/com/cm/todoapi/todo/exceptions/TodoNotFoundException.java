package com.cm.todoapi.todo.exceptions;


public class TodoNotFoundException extends RuntimeException {
    String message;
    Integer id;
    public TodoNotFoundException(String message) {
        super(message);
        this.message = message;
    }
    public TodoNotFoundException(Integer id) {
        super("Todo with id "+ id +" is not found");
        this.id = id;
        this.message = "Todo with id \"+ id +\" is not found";
    }
}
