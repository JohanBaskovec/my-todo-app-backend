package org.johan.task.controller;

import jakarta.validation.Valid;
import org.johan.task.exception.TaskNotFoundException;
import org.johan.task.model.Task;
import org.johan.task.services.TaskService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
public class TaskController {
    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping("/task")
    public List<Task> tasks() {
        return taskService.findAll();
    }

    @PostMapping("/task")
    public Task newTask(@Valid @RequestBody Task task) {
        return taskService.save(task);
    }

    @GetMapping("/task/{id}")
    public Task getOne(@PathVariable Long id) {
        return taskService.findById(id)
                .orElseThrow(() -> new TaskNotFoundException(id));
    }

    @PutMapping("/task/{id}")
    Task replaceTask(@Valid @RequestBody Task newTask, @PathVariable Long id) {
        Optional<Task> taskOptional = taskService.findById(id);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();
            task.setName(newTask.getName());
            return taskService.save(task);
        } else {
            throw new TaskNotFoundException(id);
        }
    }

    @DeleteMapping("/task/{id}")
    void deleteTask(@PathVariable Long id) {
        taskService.deleteById(id);
    }

}
