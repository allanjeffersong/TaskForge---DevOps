package com.taskforge.backend.service;

import com.taskforge.backend.dto.TaskDTO;
import com.taskforge.backend.entity.Task;
import com.taskforge.backend.exception.ResourceNotFoundException;
import com.taskforge.backend.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @InjectMocks
    private TaskService taskService;

    private Task task;
    private TaskDTO taskDTO;

    @BeforeEach
    void setUp() {
        task = new Task();
        task.setId(1L);
        task.setTitle("Tarefa de teste");
        task.setDescription("Descrição de teste");
        task.setPriority(Task.Priority.ALTA);
        task.setStatus(Task.Status.PENDENTE);

        taskDTO = new TaskDTO();
        taskDTO.setTitle("Tarefa de teste");
        taskDTO.setDescription("Descrição de teste");
        taskDTO.setPriority(Task.Priority.ALTA);
    }

    @Test
    void findAll_deveRetornarListaDeTarefas() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));

        List<TaskDTO> result = taskService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Tarefa de teste");
    }

    @Test
    void findById_deveRetornarTarefa_quandoExiste() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDTO result = taskService.findById(1L);

        assertThat(result.getTitle()).isEqualTo("Tarefa de teste");
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoEncontrada() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_deveSalvarESRetornarTarefa() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO result = taskService.create(taskDTO);

        assertThat(result.getTitle()).isEqualTo("Tarefa de teste");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void update_deveAtualizarTarefa_quandoExiste() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO updated = new TaskDTO();
        updated.setTitle("Título atualizado");
        updated.setPriority(Task.Priority.MEDIA);

        TaskDTO result = taskService.update(1L, updated);

        assertThat(result).isNotNull();
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void update_deveLancarExcecao_quandoNaoEncontrada() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(99L, taskDTO))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void delete_deveDeletarTarefa_quandoExiste() {
        when(taskRepository.existsById(1L)).thenReturn(true);

        taskService.delete(1L);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void delete_deveLancarExcecao_quandoNaoEncontrada() {
        when(taskRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> taskService.delete(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}