package com.ocp.auth_service.repository;

import com.ocp.auth_service.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(String name);
    boolean existsByName(String name);

    @Query(value = """
        SELECT r.name AS roleName, COUNT(ur.user_id) AS userCount
        FROM roles r
        LEFT JOIN user_roles ur ON ur.role_id = r.id
        GROUP BY r.name
        """, nativeQuery = true)
    List<RoleUserCountProjection> countUsersByRole();
}
