package com.taskforge.backend.service;

import com.taskforge.backend.dto.TaskDTO;
import com.taskforge.backend.entity.Task;
import com.taskforge.backend.exception.ResourceNotFoundException;
import com.taskforge.backend.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<TaskDTO> findAll() {
        return taskRepository.findAll()
                .stream()
                .map(TaskDTO::fromEntity)
                .collect(Collectors.toList());
    }

    public TaskDTO findById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com id: " + id));
        return TaskDTO.fromEntity(task);
    }

    public List<TaskDTO> findByFilters(String status, String priority) {
        List<Task> tasks;

        if (status != null && priority != null) {
            tasks = taskRepository.findByStatusAndPriority(
                    Task.Status.valueOf(status.toUpperCase()),
                    Task.Priority.valueOf(priority.toUpperCase())
            );
        } else if (status != null) {
            tasks = taskRepository.findByStatus(Task.Status.valueOf(status.toUpperCase()));
        } else if (priority != null) {
            tasks = taskRepository.findByPriority(Task.Priority.valueOf(priority.toUpperCase()));
        } else {
            tasks = taskRepository.findAll();
        }

        return tasks.stream().map(TaskDTO::fromEntity).collect(Collectors.toList());
    }

    public TaskDTO create(TaskDTO dto) {
        Task task = dto.toEntity();
        Task saved = taskRepository.save(task);
        return TaskDTO.fromEntity(saved);
    }

    public TaskDTO update(Long id, TaskDTO dto) {
        Task existing = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada com id: " + id));

        existing.setTitle(dto.getTitle());
        existing.setDescription(dto.getDescription());
        existing.setPriority(dto.getPriority());
        if (dto.getStatus() != null) {
            existing.setStatus(dto.getStatus());
        }
        existing.setDueDate(dto.getDueDate());

        Task updated = taskRepository.save(existing);
        return TaskDTO.fromEntity(updated);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Tarefa não encontrada com id: " + id);
        }
        taskRepository.deleteById(id);
    }
}