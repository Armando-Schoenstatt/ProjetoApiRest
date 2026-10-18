package com.taskmanager.api.controller;

import com.taskmanager.api.dto.TaskDTO;
import com.taskmanager.api.entity.Task;
import com.taskmanager.api.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@Tag(name = "Tasks", description = "Gerenciamento de tarefas")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Listar tarefas (filtros opcionais: status, priority)")
    public List<TaskDTO.Response> findAll(
            @RequestParam(required = false) Task.Status status,
            @RequestParam(required = false) Task.Priority priority) {
        return taskService.findAll(status, priority);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar tarefa por ID")
    public TaskDTO.Response findById(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Criar nova tarefa")
    public TaskDTO.Response create(@Valid @RequestBody TaskDTO.Request request) {
        return taskService.create(request);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar tarefa")
    public TaskDTO.Response update(@PathVariable Long id, @Valid @RequestBody TaskDTO.Request request) {
        return taskService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Deletar tarefa")
    public void delete(@PathVariable Long id) {
        taskService.delete(id);
    }
}
