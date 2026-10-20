package com.ocp.auth_service.repository;

import com.ocp.auth_service.dto.response.UserRoleAssignmentResponse;
import com.ocp.auth_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, UUID> {

    
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @Query("select u from UserCredential u where upper(u.role.name) in ('ADMIN','ROLE_ADMIN','ADMINISTRATOR','ROLE_ADMINISTRATOR','ADMINISTRATEUR','ROLE_ADMINISTRATEUR') order by u.id")
    List<UserCredential> lockAdministrators();
    Optional<UserCredential> findByEmail(String email);
    boolean existsByRole_Id(UUID roleId);
    boolean existsByEmail(String email);
    Optional<UserCredential> findByUserId(UUID userId);

    @Query("""
        SELECT new com.ocp.auth_service.dto.response.UserRoleAssignmentResponse(
            user.userId, role.id, role.name, role.description
        )
        FROM UserCredential user
        JOIN user.role role
        """)
    List<UserRoleAssignmentResponse> findAllUserRoleAssignments();

    long countByEnabledTrue();
    long countByAccountLockedTrue();
    long countByFailedAttemptsGreaterThan(int threshold);

    @Query("SELECT COALESCE(SUM(u.failedAttempts), 0) FROM UserCredential u")
    long sumFailedAttempts();
}
