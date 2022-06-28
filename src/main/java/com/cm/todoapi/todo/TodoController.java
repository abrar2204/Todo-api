package com.cm.todoapi.todo;

import com.cm.todoapi.todo.response.TodoResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
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
    ResponseEntity<TodoResponse> getAllTodos(){
        return ResponseEntity.ok(new TodoResponse(todoService.getAllTodos(),null));
    }

    @PostMapping
    ResponseEntity<TodoResponse> createNewTodo(@Valid @RequestBody Todo newTodo, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            List<String> errors = bindingResult.getAllErrors().stream().map(ObjectError::getDefaultMessage).toList();
            return ResponseEntity.unprocessableEntity().body(new TodoResponse(null, errors));
        }
        return ResponseEntity.ok(new TodoResponse(todoService.createNewTodo(newTodo),null));
    }

    @DeleteMapping(path = "{id}")
    ResponseEntity<TodoResponse> deleteATodoById(@PathVariable Integer id){
        todoService.deleteTodoById(id);
        return ResponseEntity.ok(new TodoResponse("Successfully deleted 1 todo",null));
    }

    @PutMapping(path = "{id}")
    ResponseEntity<TodoResponse> updateATodoById(@PathVariable Integer id,@RequestBody Todo todo){
        return ResponseEntity.ok(new TodoResponse(todoService.updateTodoById(id,todo),null));
    }
}
