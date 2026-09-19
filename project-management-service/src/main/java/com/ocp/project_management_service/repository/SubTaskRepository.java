package com.ocp.project_management_service.repository;


import com.ocp.project_management_service.entity.SubTask;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubTaskRepository extends JpaRepository<SubTask, UUID> {
    List<SubTask> findByTaskId(UUID taskId);

}
