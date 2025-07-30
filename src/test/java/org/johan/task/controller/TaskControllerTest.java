package org.johan.task.controller;

import org.johan.task.model.Task;
import org.johan.task.repository.TaskRepository;
import org.johan.task.services.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TaskController.class)
public class TaskControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaskService taskService;

    @MockitoBean
    private TaskRepository taskRepository;

    @Test
    void whenGetTask_thenReturnAllTasks() throws Exception {
        List<Task> expectedTasks = List.of(
                new Task("Finish this"),
                new Task("Finish this 2")
        );
        when(taskService.findAll()).thenReturn(expectedTasks);

        mockMvc.perform(get("/task"))
                .andExpect(status().isOk())
                .andExpect(content().string(""" 
                        [{"id":null,"name":"Finish this","done":false},{"id":null,"name":"Finish this 2","done":false}]"""));
    }

    @Test
    void whenPostTaskWithValidJson_thenCreateItAndReturn200() throws Exception {
        mockMvc.perform(post("/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"hello\"}")
        ).andExpect(status().isOk());
        verify(taskService).save(argThat((Task task) -> task.getName().equals("hello") && !task.isDone()));
    }

    @Test
    void whenPostTaskWithNoName_thenDoNotCreateItAndReturn400Error() throws Exception {
        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nam\": \"hello\"}")
                ).andExpect(status().is4xxClientError())
                .andExpect(content().string("""
                        {"name":"Name is mandatory"}"""));
        verify(taskService, never()).save(any());
    }

    @Test
    void whenPostTaskWithEmptyName_thenDoNotCreateItAndReturn400Error() throws Exception {
        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}")
                ).andExpect(status().is4xxClientError())
                .andExpect(content().string("""
                        {"name":"Name is mandatory"}"""));
        verify(taskService, never()).save(any());
    }

    @Test
    void whenPostTaskWithNameOver255Characters_thenDoNotCreateItAndReturn400Error() throws Exception {
        char[] longNameBytes = new char[256];
        Arrays.fill(longNameBytes, 'a');
        String longName = new String(longNameBytes);
        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "%s"}""".formatted(longName))
                ).andExpect(status().is4xxClientError())
                .andExpect(content().string("""
                        {"name":"length must be between 0 and 255"}"""));
        verify(taskService, never()).save(any());
    }

    @Test
    void whenGetTaskById_thenReturnTask() throws Exception {
        Task expectedTask = new Task("Hi");
        expectedTask.setId(4L);
        when(taskService.findById(4L)).thenReturn(Optional.of(expectedTask));

        mockMvc.perform(get("/task/4"))
                .andExpect(status().isOk())
                .andExpect(content().string("""
                        {"id":4,"name":"Hi","done":false}"""));
    }

    @Test
    void whenGetTaskByIdThatDoesntExist_thenReturnNothing() throws Exception {
        when(taskService.findById(4L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/task/4"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("""
                        {"error":"TASK with id 4 not found."}"""));
    }

    @Test
    void whenDeleteTaskById_thenDeleteIt() throws Exception {
        mockMvc.perform(delete("/task/4"))
                .andExpect(status().isOk());

        verify(taskService, times(1)).deleteById(4L);
    }

    @Test
    void whenPutTaskWithValidJson_thenUpdateItAndReturn200() throws Exception {
        Task oldTask = new Task("Hi");
        oldTask.setId(4L);
        when(taskService.findById(eq(4L))).thenReturn(Optional.of(oldTask));

        mockMvc.perform(put("/task/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"hello\"}")
        ).andExpect(status().isOk());
        verify(taskService).save(argThat((Task task) -> task.getId() == 4L && task.getName().equals("hello") && !task.isDone()));
    }
}
