package com.ocp.organisation_service.repository;

import com.ocp.organisation_service.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, UUID> {

	boolean existsByEmail(String email);

	Optional<UserProfile> findByEmail(String email);

	Optional<UserProfile> findByAuthUserId(UUID authUserId);
}
