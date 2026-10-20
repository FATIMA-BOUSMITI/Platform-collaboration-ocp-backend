package com.ocp.project_management_service.repository;
import com.ocp.project_management_service.entity.CollaborationOutbox;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
import java.time.LocalDateTime;
public interface CollaborationOutboxRepository extends JpaRepository<CollaborationOutbox,UUID>{boolean existsByDedupKey(String key);List<CollaborationOutbox> findTop50ByDeliveredAtIsNullAndNextAttemptAtBeforeOrderByCreatedAtAsc(LocalDateTime now);}
