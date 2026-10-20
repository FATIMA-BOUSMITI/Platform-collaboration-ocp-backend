package com.ocp.project_management_service.repository;

import com.ocp.project_management_service.entity.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ProjectRepository extends JpaRepository<Project, UUID> {
    List<Project> findByDepartmentId(UUID deptId);
    List<Project> findByResponsibleId(UUID respId);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select p from Project p where p.id = :id")
    java.util.Optional<Project> lockById(@org.springframework.data.repository.query.Param("id") UUID id);
    boolean existsById(UUID id) ;
}
