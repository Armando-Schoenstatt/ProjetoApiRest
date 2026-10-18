package com.taskmanager.api.repository;

import com.taskmanager.api.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByUserId(Long userId);
    List<Task> findByUserIdAndStatus(Long userId, Task.Status status);
    List<Task> findByUserIdAndPriority(Long userId, Task.Priority priority);
    Optional<Task> findByIdAndUserId(Long id, Long userId);
}
