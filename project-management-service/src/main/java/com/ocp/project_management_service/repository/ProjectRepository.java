package com.ocp.project_management_service.repository;

import com.ocp.project_management_service.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByDepartmentId(UUID deptId);
    List<Project> findByResponsibleId(UUID respId);
    boolean existsById(UUID id) ;
}
