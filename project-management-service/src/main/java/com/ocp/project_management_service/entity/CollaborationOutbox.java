package com.ocp.project_management_service.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;
@Entity @Table(name="collaboration_outbox") @Data
public class CollaborationOutbox {@Id private UUID id;@Column(nullable=false,unique=true)private String dedupKey;@Column(nullable=false,columnDefinition="TEXT")private String payload;private LocalDateTime createdAt;private LocalDateTime nextAttemptAt;private LocalDateTime deliveredAt;private int attempts;}
