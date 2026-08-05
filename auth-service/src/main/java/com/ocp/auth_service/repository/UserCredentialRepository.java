package com.ocp.auth_service.Repository;

import com.ocp.auth_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface UserCredentialRepository extends JpaRepository<UserCredential, UUID> {

    Optional<UserCredential> findByEmail(String email);
    boolean existsByEmail(String email);
    long countByEnabledTrue();
    long countByAccountLockedTrue();
    long countByFailedAttemptsGreaterThan(int threshold);
    @Query("SELECT COALESCE(SUM(u.failedAttempts), 0) FROM UserCredential u")
    long sumFailedAttempts();

}
