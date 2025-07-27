package org.johan.todo.services;

import org.johan.todo.model.Todo;
import org.johan.todo.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }


    public List<Todo> findAll() {
        return todoRepository.findAll();
    }
}
