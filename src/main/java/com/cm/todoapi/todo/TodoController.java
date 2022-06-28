package com.cm.todoapi.todo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/api/todo")
public class TodoController {

    private final TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    List<Todo> getAllTodos(){
        return todoService.getAllTodos();
    }

    @PostMapping
    Todo createNewTodo(@RequestBody Todo newTodo){
        return  todoService.createNewTodo(newTodo);
    }
}
