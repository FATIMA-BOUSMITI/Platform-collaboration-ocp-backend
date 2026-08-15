package com.ocp.auth_service.repository;



import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
    Optional<Permission> findByName (String name);
    boolean existsByName(String name );
}
