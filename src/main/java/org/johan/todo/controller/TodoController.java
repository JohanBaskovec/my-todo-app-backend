package org.johan.todo.controller;

import jakarta.validation.Valid;
import org.johan.todo.exception.TodoNotFoundException;
import org.johan.todo.model.Todo;
import org.johan.todo.services.TodoService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class TodoController {
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/todo")
    public List<Todo> todos() {
        return todoService.findAll();
    }

    @PostMapping("/todo")
    public Todo newTodo(@Valid @RequestBody Todo todo) {
        return todoService.save(todo);
    }

    @GetMapping("/todo/{id}")
    public Todo getOne(@PathVariable Long id) {
        return todoService.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));
    }

    @PutMapping("/todo/{id}")
    Todo replaceTodo(@Valid @RequestBody Todo newTodo, @PathVariable Long id) {
        Optional<Todo> todoOptional = todoService.findById(id);
        if (todoOptional.isPresent()) {
            Todo todo = todoOptional.get();
            todo.setName(newTodo.getName());
            return todoService.save(todo);
        } else {
            throw new TodoNotFoundException(id);
        }
    }

    @DeleteMapping("/todo/{id}")
    void deleteTodo(@PathVariable Long id) {
        todoService.deleteById(id);
    }

}
