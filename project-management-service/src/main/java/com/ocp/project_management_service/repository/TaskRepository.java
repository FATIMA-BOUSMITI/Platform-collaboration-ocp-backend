package com.ocp.project_management_service.repository;

import com.ocp.project_management_service.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByProjectIdAndStatus(UUID id, Task.TaskStatus status);
    List<Task> findByAssigneeId(UUID userId);
    List<Task> findByProjectId(UUID id);
    boolean existsByProjectIdAndAssigneeId(UUID projectId, UUID userId);

    @Query("select distinct task.project.id from Task task where task.assigneeId = :userId")
    List<UUID> findDistinctProjectIdsByAssigneeId(@Param("userId") UUID userId);
}
