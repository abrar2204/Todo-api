package com.cm.todoapi.todo;

import com.cm.todoapi.todo.exceptions.TodoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    TodoRepository todoRepository;

    @Autowired
    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public List<Todo> getAllTodos() {
        return todoRepository.findAll();
    }

    public Todo createNewTodo(Todo newTodo) {
        return todoRepository.save(newTodo);
    }

    public void deleteTodoById(Integer id) {
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException("Todo with id "+ id +" is not found"));
        todoRepository.delete(todo);
    }
}
