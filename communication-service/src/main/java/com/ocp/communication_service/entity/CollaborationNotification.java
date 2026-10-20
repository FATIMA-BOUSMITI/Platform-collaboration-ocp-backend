package com.ocp.communication_service.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;
@Entity @Table(name="collaboration_notifications") @Data
public class CollaborationNotification {
 @Id private UUID id;@Column(nullable=false,unique=true)private UUID externalId;@Column(nullable=false)private UUID userId;@Column(nullable=false,length=40)private String type;@Column(nullable=false,length=150)private String title;@Column(nullable=false,length=500)private String body;@Column(nullable=false,length=500)private String link;@Column(name="is_read",nullable=false)private boolean read;@Column(nullable=false)private LocalDateTime createdAt;
}
