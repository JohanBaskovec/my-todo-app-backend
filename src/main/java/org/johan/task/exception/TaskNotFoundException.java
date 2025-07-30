package org.johan.task.exception;

public class TaskNotFoundException extends RuntimeException {
    public TaskNotFoundException(long id) {
        super("TASK with id " + id + " not found.");
    }
}
