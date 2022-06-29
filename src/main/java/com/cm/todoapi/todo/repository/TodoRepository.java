package com.cm.todoapi.todo.repository;

import com.cm.todoapi.todo.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo,Integer> {
}
