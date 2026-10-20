package com.ocp.communication_service.repository;
import com.ocp.communication_service.entity.CollaborationNotification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface NotificationRepository extends JpaRepository<CollaborationNotification,UUID>{
 Optional<CollaborationNotification> findByExternalId(UUID externalId);
 List<CollaborationNotification> findByUserIdOrderByCreatedAtDesc(UUID userId,Pageable page);
 List<CollaborationNotification> findByUserIdAndReadFalseOrderByCreatedAtDesc(UUID userId,Pageable page);
 long countByUserIdAndReadFalse(UUID userId);
 @Modifying @Query("update CollaborationNotification n set n.read=true where n.userId=:user")void readAll(@Param("user")UUID user);
}
