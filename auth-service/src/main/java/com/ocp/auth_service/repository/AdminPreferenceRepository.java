package com.ocp.auth_service.repository;
import com.ocp.auth_service.entity.AdminPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface AdminPreferenceRepository extends JpaRepository<AdminPreference,UUID>{}
