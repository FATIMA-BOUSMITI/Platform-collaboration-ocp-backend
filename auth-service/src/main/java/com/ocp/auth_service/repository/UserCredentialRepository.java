package com.ocp.auth_service.repository;

import com.ocp.auth_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserCredentialRepository extends JpaRepository<UserCredential, UUID> {

    Optional<UserCredential> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserCredential> findByUserId(UUID userId);

    long countByEnabledTrue();
    long countByAccountLockedTrue();
    long countByFailedAttemptsGreaterThan(int threshold);

    @Query("SELECT COALESCE(SUM(u.failedAttempts), 0) FROM UserCredential u")
    long sumFailedAttempts();
}
