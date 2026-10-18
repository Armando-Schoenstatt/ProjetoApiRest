package com.taskmanager.api.service;

import com.taskmanager.api.dto.TaskDTO;
import com.taskmanager.api.entity.Task;
import com.taskmanager.api.entity.User;
import com.taskmanager.api.exception.GlobalExceptionHandler.ResourceNotFoundException;
import com.taskmanager.api.repository.TaskRepository;
import com.taskmanager.api.repository.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<TaskDTO.Response> findAll(Task.Status status, Task.Priority priority) {
        Long userId = currentUser().getId();
        List<Task> tasks;
        if (status != null) tasks = taskRepository.findByUserIdAndStatus(userId, status);
        else if (priority != null) tasks = taskRepository.findByUserIdAndPriority(userId, priority);
        else tasks = taskRepository.findByUserId(userId);
        return tasks.stream().map(TaskDTO.Response::from).toList();
    }

    public TaskDTO.Response findById(Long id) {
        return TaskDTO.Response.from(getOwnedTask(id));
    }

    public TaskDTO.Response create(TaskDTO.Request request) {
        Task task = new Task();
        applyRequest(task, request);
        task.setUser(currentUser());
        return TaskDTO.Response.from(taskRepository.save(task));
    }

    public TaskDTO.Response update(Long id, TaskDTO.Request request) {
        Task task = getOwnedTask(id);
        applyRequest(task, request);
        return TaskDTO.Response.from(taskRepository.save(task));
    }

    public void delete(Long id) {
        taskRepository.delete(getOwnedTask(id));
    }

    private void applyRequest(Task task, TaskDTO.Request req) {
        task.setTitle(req.title());
        task.setDescription(req.description());
        if (req.status() != null) task.setStatus(req.status());
        if (req.priority() != null) task.setPriority(req.priority());
        task.setDueDate(req.dueDate());
    }

    private Task getOwnedTask(Long id) {
        return taskRepository.findByIdAndUserId(id, currentUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Tarefa não encontrada: " + id));
    }

    private User currentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
    }
}
