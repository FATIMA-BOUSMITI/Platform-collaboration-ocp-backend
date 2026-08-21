package com.ocp.auth_service.repository;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.ocp.auth_service.entity.UserCredential;

@Repository

public interface UserCredentialRepository
	extends JpaRepository<UserCredential, UUID> {

	Optional<UserCredential> findByEmail(String email);
    Optional<UserCredential> findByUserId(UUID userId);
    boolean existsByEmail(String email);
    long countByEnabledTrue();
    long countByAccountLockedTrue();
    long countByFailedAttemptsGreaterThan(int threshold);
    @Query("SELECT COALESCE(SUM(u.failedAttempts), 0) FROM UserCredential u")
    long sumFailedAttempts();


}
