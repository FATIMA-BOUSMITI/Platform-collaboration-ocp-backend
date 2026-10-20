package com.ocp.project_management_service.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;
@Entity @Table(name="project_invitations") @Data
public class ProjectInvitation {
 public enum Status {PENDING,ACCEPTED,DECLINED,CANCELLED}
 @Id private UUID id;
 @Column(nullable=false)private UUID projectId;
 @Column(nullable=false)private UUID userId;
 @Column(nullable=false)private UUID invitedBy;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20)private Status status;
 @Column(nullable=false)private LocalDateTime createdAt;
 private LocalDateTime respondedAt;
 @Version private long version;
}
