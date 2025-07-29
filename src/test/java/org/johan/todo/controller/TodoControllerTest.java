package org.johan.todo.controller;

import org.johan.todo.model.Todo;
import org.johan.todo.repository.TodoRepository;
import org.johan.todo.services.TodoService;
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

@WebMvcTest(TodoController.class)
public class TodoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TodoService todoService;

    @MockitoBean
    private TodoRepository todoRepository;

    @Test
    void whenGetTodo_thenReturnAllTodos() throws Exception {
        List<Todo> expectedTodos = List.of(
                new Todo("Finish this"),
                new Todo("Finish this 2")
        );
        when(todoService.findAll()).thenReturn(expectedTodos);

        mockMvc.perform(get("/todo"))
                .andExpect(status().isOk())
                .andExpect(content().string(""" 
                        [{"id":null,"name":"Finish this","done":false},{"id":null,"name":"Finish this 2","done":false}]"""));
    }

    @Test
    void whenPostTodoWithValidJson_thenCreateItAndReturn200() throws Exception {
        mockMvc.perform(post("/todo")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"hello\"}")
        ).andExpect(status().isOk());
        verify(todoService).save(argThat((Todo todo) -> todo.getName().equals("hello") && !todo.isDone()));
    }

    @Test
    void whenPostTodoWithNoName_thenDoNotCreateItAndReturn400Error() throws Exception {
        mockMvc.perform(post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nam\": \"hello\"}")
                ).andExpect(status().is4xxClientError())
                .andExpect(content().string("""
                        {"name":"Name is mandatory"}"""));
        verify(todoService, never()).save(any());
    }

    @Test
    void whenPostTodoWithEmptyName_thenDoNotCreateItAndReturn400Error() throws Exception {
        mockMvc.perform(post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"\"}")
                ).andExpect(status().is4xxClientError())
                .andExpect(content().string("""
                        {"name":"Name is mandatory"}"""));
        verify(todoService, never()).save(any());
    }

    @Test
    void whenPostTodoWithNameOver255Characters_thenDoNotCreateItAndReturn400Error() throws Exception {
        char[] longNameBytes = new char[256];
        Arrays.fill(longNameBytes, 'a');
        String longName = new String(longNameBytes);
        mockMvc.perform(post("/todo")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "%s"}""".formatted(longName))
                ).andExpect(status().is4xxClientError())
                .andExpect(content().string("""
                        {"name":"length must be between 0 and 255"}"""));
        verify(todoService, never()).save(any());
    }

    @Test
    void whenGetTodoById_thenReturnTodo() throws Exception {
        Todo expectedTodo = new Todo("Hi");
        expectedTodo.setId(4L);
        when(todoService.findById(4L)).thenReturn(Optional.of(expectedTodo));

        mockMvc.perform(get("/todo/4"))
                .andExpect(status().isOk())
                .andExpect(content().string("""
                        {"id":4,"name":"Hi","done":false}"""));
    }

    @Test
    void whenGetTodoByIdThatDoesntExist_thenReturnNothing() throws Exception {
        when(todoService.findById(4L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/todo/4"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("""
                        {"error":"TODO with id 4 not found."}"""));
    }

    @Test
    void whenDeleteTodoById_thenDeleteIt() throws Exception {
        mockMvc.perform(delete("/todo/4"))
                .andExpect(status().isOk());

        verify(todoService, times(1)).deleteById(4L);
    }

    @Test
    void whenPutTodoWithValidJson_thenUpdateItAndReturn200() throws Exception {
        Todo oldTodo = new Todo("Hi");
        oldTodo.setId(4L);
        when(todoService.findById(eq(4L))).thenReturn(Optional.of(oldTodo));

        mockMvc.perform(put("/todo/4")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\": \"hello\"}")
        ).andExpect(status().isOk());
        verify(todoService).save(argThat((Todo todo) -> todo.getId() == 4L && todo.getName().equals("hello") && !todo.isDone()));
    }
}
