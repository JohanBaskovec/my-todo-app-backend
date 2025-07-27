package org.johan.todo;

import org.johan.todo.model.Todo;
import org.johan.todo.repository.TodoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;

@SpringBootApplication
public class TodoApplication {
    private static final Logger log = LoggerFactory.getLogger(TodoApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TodoApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner demo(TodoRepository todoRepository) {
        return (args) -> {
            // save a few customers
            log.info("Creating demo TODOs");
            todoRepository.save(new Todo("Finish portfolio"));
            todoRepository.save(new Todo("Apply for jobs"));
        };
    }
}