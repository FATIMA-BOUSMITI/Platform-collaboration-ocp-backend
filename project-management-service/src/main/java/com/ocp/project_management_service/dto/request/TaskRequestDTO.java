package com.ocp.project_management_service.dto.request;

import com.ocp.project_management_service.entity.Project;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class TaskRequestDTO {

    @NotBlank(message = "Le titre de la tâche est obligatoire")
    private String title;
    private String description;
    private UUID assigneeId;
    @NotNull
    private Project.ProjectPriority priority;
    private LocalDate dueDate;
    private Long estimatedTimeMinutes; // Duration exprimée en minutes côté DTO
}
