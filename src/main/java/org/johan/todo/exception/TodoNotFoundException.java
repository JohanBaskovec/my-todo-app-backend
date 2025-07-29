package org.johan.todo.exception;

public class TodoNotFoundException extends RuntimeException {
    public TodoNotFoundException(long id) {
        super("TODO with id " + id + " not found.");
    }
}
