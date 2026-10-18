package com.taskmanager.api.dto;

import com.taskmanager.api.entity.Task;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TaskDTO {

    public record Request(
            @NotBlank @Size(max = 150) String title,
            String description,
            Task.Status status,
            Task.Priority priority,
            LocalDate dueDate
    ) {}

    public record Response(
            Long id,
            String title,
            String description,
            Task.Status status,
            Task.Priority priority,
            LocalDate dueDate,
            LocalDateTime createdAt,
            LocalDateTime updatedAt
    ) {
        public static Response from(Task task) {
            return new Response(
                    task.getId(),
                    task.getTitle(),
                    task.getDescription(),
                    task.getStatus(),
                    task.getPriority(),
                    task.getDueDate(),
                    task.getCreatedAt(),
                    task.getUpdatedAt()
            );
        }
    }
}
