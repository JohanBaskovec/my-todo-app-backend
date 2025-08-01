package org.johan.task.controller;

import org.johan.task.model.Task;
import org.johan.task.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class TaskControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
    }

    @Test
    void getTaskShouldReturnAllTasksSortedByDescendingCreationDate() {
        // We test only creationDate, not lastModificationDate
        List<Task> testTasks = List.of(new Task("Task n°1"), new Task("Task n°2"));
        taskRepository.saveAll(testTasks);
        ResponseEntity<List<Task>> response = restTemplate.exchange("http://localhost:" + port + "/task", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Task> tasks = response.getBody();
        assertThat(tasks).hasSize(2);

        assertThat(tasks.get(0).getName()).isEqualTo(testTasks.get(1).getName());
        assertThat(tasks.get(1).getName()).isEqualTo(testTasks.get(0).getName());

        assertFalse(tasks.get(0).isDone());
        assertFalse(tasks.get(1).isDone());

        assertThat(tasks.get(0).getId()).isGreaterThan(tasks.get(1).getId());
        assertThat(tasks.get(0).getCreationDateTime()).isAfter(tasks.get(1).getCreationDateTime());
    }

    @Test
    void getTaskShouldReturnAllTasksSortedByDescendingCreationDateAndLastModificationDate() {
        // We test that updating a Task puts it at the beginning of the returned list
        List<Task> testTasks = List.of(new Task("Task n°1"), new Task("Task n°2"), new Task("Task n°3"), new Task("Task n°4"));
        taskRepository.saveAll(testTasks);
        testTasks.get(2).setDone(true);
        taskRepository.save(testTasks.get(2));
        testTasks.get(2).setDone(false);
        taskRepository.save(testTasks.get(2));

        ResponseEntity<List<Task>> response = restTemplate.exchange("http://localhost:" + port + "/task", HttpMethod.GET, null, new ParameterizedTypeReference<>() {});

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Task> tasks = response.getBody();
        assertThat(tasks).hasSize(4);

        assertThat(tasks.get(0).getName()).isEqualTo(testTasks.get(2).getName());
        assertThat(tasks.get(1).getName()).isEqualTo(testTasks.get(3).getName());
        assertThat(tasks.get(2).getName()).isEqualTo(testTasks.get(1).getName());
        assertThat(tasks.get(3).getName()).isEqualTo(testTasks.get(0).getName());

        assertFalse(tasks.get(0).isDone());
        assertFalse(tasks.get(1).isDone());
        assertFalse(tasks.get(2).isDone());
        assertFalse(tasks.get(3).isDone());

        assertThat(tasks.get(0).getCreationDateTime()).isBefore(tasks.get(1).getCreationDateTime());
        assertThat(tasks.get(1).getCreationDateTime()).isAfter(tasks.get(2).getCreationDateTime());
        assertThat(tasks.get(2).getCreationDateTime()).isAfter(tasks.get(3).getCreationDateTime());
        
        assertThat(tasks.get(0).getLastModificationDateTime()).isAfter(tasks.get(1).getLastModificationDateTime());
        assertThat(tasks.get(1).getLastModificationDateTime()).isAfter(tasks.get(2).getLastModificationDateTime());
        assertThat(tasks.get(2).getLastModificationDateTime()).isAfter(tasks.get(3).getLastModificationDateTime());
    }
}
