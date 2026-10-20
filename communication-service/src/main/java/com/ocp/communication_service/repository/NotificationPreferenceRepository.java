package com.ocp.communication_service.repository;
import com.ocp.communication_service.entity.NotificationPreference;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference,UUID>{}
