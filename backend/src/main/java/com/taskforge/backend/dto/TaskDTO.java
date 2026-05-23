package com.taskforge.backend.dto;

import com.taskforge.backend.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskDTO {

    private Long id;

    @NotBlank(message = "Título é obrigatório")
    private String title;

    private String description;

    @NotNull(message = "Prioridade é obrigatória")
    private Task.Priority priority;

    private Task.Status status;

    private LocalDate dueDate;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Static factory: Entity → DTO
    public static TaskDTO fromEntity(Task task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setPriority(task.getPriority());
        dto.setStatus(task.getStatus());
        dto.setDueDate(task.getDueDate());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setUpdatedAt(task.getUpdatedAt());
        return dto;
    }

    // Convert DTO → Entity
    public Task toEntity() {
        Task task = new Task();
        task.setTitle(this.title);
        task.setDescription(this.description);
        task.setPriority(this.priority);
        if (this.status != null) {
            task.setStatus(this.status);
        }
        task.setDueDate(this.dueDate);
        return task;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Task.Priority getPriority() { return priority; }
    public void setPriority(Task.Priority priority) { this.priority = priority; }

    public Task.Status getStatus() { return status; }
    public void setStatus(Task.Status status) { this.status = status; }

    public LocalDate getDueDate() { return dueDate; }
    public void setDueDate(LocalDate dueDate) { this.dueDate = dueDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}