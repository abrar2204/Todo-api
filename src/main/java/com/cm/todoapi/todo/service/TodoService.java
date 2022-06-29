package com.cm.todoapi.todo.service;

import com.cm.todoapi.todo.model.Todo;
import com.cm.todoapi.todo.exceptions.TodoNotFoundException;
import com.cm.todoapi.todo.repository.TodoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        Todo todo = todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
        todoRepository.delete(todo);
    }
    @Transactional
    public Todo updateTodoById(Integer id,Todo todo) {
        Todo newTodo = todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));

        newTodo.setTitle(todo.getTitle());
        newTodo.setDescription(todo.getDescription());
        newTodo.setCompleted(todo.isCompleted());
        newTodo.setCreatedAt(todo.getCreatedAt());

        return newTodo;
    }
}
