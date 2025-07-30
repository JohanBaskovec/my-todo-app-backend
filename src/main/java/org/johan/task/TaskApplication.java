package org.johan.task;

import org.johan.task.model.Task;
import org.johan.task.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class TaskApplication {
    private static final Logger log = LoggerFactory.getLogger(TaskApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TaskApplication.class, args);
    }

    @Bean
    @Profile("!test")
    public CommandLineRunner demo(TaskRepository taskRepository) {
        return (args) -> {
            // save a few customers
            log.info("Creating demo TASKs");
            taskRepository.save(new Task("Finish portfolio"));
            taskRepository.save(new Task("Apply for jobs"));
        };
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/*").allowedOrigins("*");
            }
        };
    }
}