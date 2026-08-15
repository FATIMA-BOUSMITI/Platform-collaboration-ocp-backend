package com.ocp.project_management_service.repository;

import com.ocp.project_management_service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProjectIdAndStatus(UUID id, Task.TaskStatus status);
    List<Task> findByAssigneeId(UUID userId);
    List<Task> findByProjectId(UUID id);
}
