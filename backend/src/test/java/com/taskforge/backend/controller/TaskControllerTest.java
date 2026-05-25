package com.taskforge.backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskforge.backend.dto.TaskDTO;
import com.taskforge.backend.entity.Task;
import com.taskforge.backend.service.TaskService;
import com.taskforge.backend.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    private TaskDTO buildDTO() {
        TaskDTO dto = new TaskDTO();
        dto.setId(1L);
        dto.setTitle("Tarefa teste");
        dto.setPriority(Task.Priority.ALTA);
        dto.setStatus(Task.Status.PENDENTE);
        return dto;
    }

    @Test
    void getAll_deveRetornar200() throws Exception {
        List<TaskDTO> tasks = Arrays.asList(buildDTO());
        when(taskService.findByFilters(null, null)).thenReturn(tasks);

        mockMvc.perform(get("/api/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Tarefa teste"));
    }

    @Test
    void getById_deveRetornar200_quandoExiste() throws Exception {
        when(taskService.findById(1L)).thenReturn(buildDTO());

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Tarefa teste"));
    }

    @Test
    void getById_deveRetornar404_quandoNaoEncontrada() throws Exception {
        when(taskService.findById(99L)).thenThrow(new ResourceNotFoundException("Tarefa não encontrada com id: 99"));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_deveRetornar201() throws Exception {
        TaskDTO dto = buildDTO();
        when(taskService.create(any(TaskDTO.class))).thenReturn(dto);

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Tarefa teste"));
    }

    @Test
    void create_deveRetornar400_quandoTituloVazio() throws Exception {
        TaskDTO dto = new TaskDTO();
        dto.setPriority(Task.Priority.ALTA);
        // title ausente → deve falhar validação

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_deveRetornar200() throws Exception {
        TaskDTO dto = buildDTO();
        when(taskService.update(eq(1L), any(TaskDTO.class))).thenReturn(dto);

        mockMvc.perform(put("/api/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }

    @Test
    void delete_deveRetornar204() throws Exception {
        doNothing().when(taskService).delete(1L);

        mockMvc.perform(delete("/api/tasks/1"))
                .andExpect(status().isNoContent());
    }
}