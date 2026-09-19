package com.ocp.project_management_service.dto.request;

import com.ocp.project_management_service.entity.Project;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
@Getter
@Setter

public class ProjectUpdateRequestDTO {
    private String name;
    private String description;
    private UUID responsibleId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Project.ProjectPriority projectPriority;
    private BigDecimal budget;
}
