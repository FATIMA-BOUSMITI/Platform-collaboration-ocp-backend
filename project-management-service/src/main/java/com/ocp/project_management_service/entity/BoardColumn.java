package com.ocp.project_management_service.entity;
import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;
@Entity @Table(name="board_columns",indexes=@Index(name="idx_board_columns_project",columnList="projectId")) @Data
public class BoardColumn {
 @Id private UUID id;
 @Column(nullable=false) private UUID projectId;
 @Column(nullable=false,length=100) private String name;
 @Enumerated(EnumType.STRING) @Column(nullable=false) private Task.TaskStatus status;
 @Column(nullable=false) private int position;
}
