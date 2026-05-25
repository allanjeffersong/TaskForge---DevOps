package com.taskforge.backend.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskforge.backend.controller.TaskController;
import com.taskforge.backend.service.TaskService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TaskController.class)
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void handleNotFound_deveRetornar404_comMensagem() throws Exception {
        when(taskService.findById(99L))
                .thenThrow(new ResourceNotFoundException("Tarefa não encontrada com id: 99"));

        mockMvc.perform(get("/api/tasks/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Tarefa não encontrada com id: 99"));
    }

    @Test
    void handleValidation_deveRetornar400_quandoCamposInvalidos() throws Exception {
        String payload = "{\"priority\": \"ALTA\"}";
        // title ausente — dispara @NotBlank

        mockMvc.perform(post("/api/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.message").value("Erro de validação"));
    }

    @Test
    void handleIllegalArgument_deveRetornar400_quandoEnumInvalido() throws Exception {
        when(taskService.findByFilters(any(), any()))
                .thenThrow(new IllegalArgumentException("No enum constant INVALIDO"));

        mockMvc.perform(get("/api/tasks?status=INVALIDO"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void handleGeneral_deveRetornar500_quandoErroInesperado() throws Exception {
        when(taskService.findById(anyLong()))
                .thenThrow(new RuntimeException("Erro inesperado"));

        mockMvc.perform(get("/api/tasks/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("Erro interno do servidor"));
    }
}