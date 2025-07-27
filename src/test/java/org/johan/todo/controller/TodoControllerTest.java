package org.johan.todo.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.johan.todo.model.Todo;
import org.johan.todo.repository.TodoRepository;
import org.johan.todo.services.TodoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class TodoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TodoService todoService;

    @MockBean
    private TodoRepository todoRepository;

    @Test
    void testGetAllTodos() throws Exception {
        List<Todo> expectedTodos = List.of(
                new Todo("Finish this"),
                new Todo("Finish this 2")
        );
        when(todoService.findAll()).thenReturn(expectedTodos);

        MvcResult result = mockMvc.perform(get("/todo"))
                .andExpect(status().isOk())
                .andReturn();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = result.getResponse().getContentAsString();
        List<Todo> actual = objectMapper.readValue(json, new TypeReference<>() {});
        assertEquals(expectedTodos, actual);
    }
}
