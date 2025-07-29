package org.johan.todo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

import java.util.Objects;

@Entity
public class Todo {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "Name is mandatory")
    @Length(max=255)
    private String name;

    private boolean done = false;

    protected Todo() {

    }

    public Todo(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "Todo{" +
                "id=" + id +
                ", title='" + name + '\'' +
                ", done=" + done +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Todo todo = (Todo) o;
        return done == todo.done && Objects.equals(id, todo.id) && Objects.equals(name, todo.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, done);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
