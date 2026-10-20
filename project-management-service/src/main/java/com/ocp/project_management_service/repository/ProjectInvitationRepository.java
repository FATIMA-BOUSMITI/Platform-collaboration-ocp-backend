package com.ocp.project_management_service.repository;
import com.ocp.project_management_service.entity.ProjectInvitation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface ProjectInvitationRepository extends JpaRepository<ProjectInvitation,UUID>{
 List<ProjectInvitation> findByUserIdAndStatusOrderByCreatedAtDesc(UUID user,ProjectInvitation.Status status);
 List<ProjectInvitation> findByProjectIdOrderByCreatedAtDesc(UUID project);
 boolean existsByProjectIdAndUserIdAndStatus(UUID project,UUID user,ProjectInvitation.Status status);
}
