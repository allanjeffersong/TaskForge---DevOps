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
import java.util.Collections;
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
        taskDTO.setStatus(Task.Status.PENDENTE);
    }

    // ==================== findAll ====================

    @Test
    void findAll_deveRetornarListaDeTarefas() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));

        List<TaskDTO> result = taskService.findAll();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo("Tarefa de teste");
    }

    @Test
    void findAll_deveRetornarListaVazia_quandoNaoHaTarefas() {
        when(taskRepository.findAll()).thenReturn(Collections.emptyList());

        List<TaskDTO> result = taskService.findAll();

        assertThat(result).isEmpty();
    }

    // ==================== findById ====================

    @Test
    void findById_deveRetornarTarefa_quandoExiste() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));

        TaskDTO result = taskService.findById(1L);

        assertThat(result.getTitle()).isEqualTo("Tarefa de teste");
        assertThat(result.getPriority()).isEqualTo(Task.Priority.ALTA);
    }

    @Test
    void findById_deveLancarExcecao_quandoNaoEncontrada() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==================== findByFilters ====================

    @Test
    void findByFilters_semFiltros_deveRetornarTodas() {
        when(taskRepository.findAll()).thenReturn(Arrays.asList(task));

        List<TaskDTO> result = taskService.findByFilters(null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void findByFilters_comStatus_deveRetornarFiltradas() {
        when(taskRepository.findByStatus(Task.Status.PENDENTE)).thenReturn(Arrays.asList(task));

        List<TaskDTO> result = taskService.findByFilters("PENDENTE", null);

        assertThat(result).hasSize(1);
        verify(taskRepository).findByStatus(Task.Status.PENDENTE);
    }

    @Test
    void findByFilters_comPriority_deveRetornarFiltradas() {
        when(taskRepository.findByPriority(Task.Priority.ALTA)).thenReturn(Arrays.asList(task));

        List<TaskDTO> result = taskService.findByFilters(null, "ALTA");

        assertThat(result).hasSize(1);
        verify(taskRepository).findByPriority(Task.Priority.ALTA);
    }

    @Test
    void findByFilters_comStatusEPriority_deveRetornarFiltradas() {
        when(taskRepository.findByStatusAndPriority(Task.Status.PENDENTE, Task.Priority.ALTA))
                .thenReturn(Arrays.asList(task));

        List<TaskDTO> result = taskService.findByFilters("PENDENTE", "ALTA");

        assertThat(result).hasSize(1);
        verify(taskRepository).findByStatusAndPriority(Task.Status.PENDENTE, Task.Priority.ALTA);
    }

    // ==================== create ====================

    @Test
    void create_deveSalvarERetornarTarefa() {
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO result = taskService.create(taskDTO);

        assertThat(result.getTitle()).isEqualTo("Tarefa de teste");
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    // ==================== update ====================

    @Test
    void update_deveAtualizarTarefa_quandoExiste() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO updated = new TaskDTO();
        updated.setTitle("Título atualizado");
        updated.setPriority(Task.Priority.MEDIA);
        updated.setStatus(Task.Status.EM_ANDAMENTO);

        TaskDTO result = taskService.update(1L, updated);

        assertThat(result).isNotNull();
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void update_deveAtualizarTarefa_semStatus() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);

        TaskDTO updated = new TaskDTO();
        updated.setTitle("Título atualizado");
        updated.setPriority(Task.Priority.BAIXA);
        // status null — não deve alterar o status existente

        TaskDTO result = taskService.update(1L, updated);

        assertThat(result).isNotNull();
        verify(taskRepository, times(1)).save(any(Task.class));
    }

    @Test
    void update_deveLancarExcecao_quandoNaoEncontrada() {
        when(taskRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> taskService.update(99L, taskDTO))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    // ==================== delete ====================

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
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }
}