package com.ocp.auth_service.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDateTime;
@Entity @Table(name="admin_notices") @Data
public class AdminNotice { @Id @GeneratedValue(strategy=GenerationType.UUID) private UUID id; @Column(nullable=false) private UUID owner; @Column(nullable=false) private String message; private LocalDateTime createdAt=LocalDateTime.now(); @Column(name="is_read") private boolean read; }
