package com.ocp.auth_service.repository;



import java.util.Optional;
import java.util.UUID;

import com.ocp.auth_service.entity.Permission;
import com.ocp.auth_service.entity.Role;
import com.ocp.auth_service.entity.UserCredential;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import com.ocp.auth_service.entity.Role;
@Repository
public interface PermissionRepository
	extends JpaRepository<Permission, UUID> {

	Optional<Permission> findByName(String name);

	boolean existsByName(String name);




}
