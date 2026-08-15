package com.ocp.project_management_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
@Getter
@Setter
public class TaskResponseDTO {
    private UUID id;
    private UUID projectId;
    private String title;
    private String description;
    private UUID assigneeId;
    private String priority;
    private LocalDate dueDate;
    private Long estimatedTimeMinutes;
    private Long realTimeMinutes;
    private String status;
    private List<SubTaskDTO> subTasks;
    private List<CommentDTO> comments ;
}
