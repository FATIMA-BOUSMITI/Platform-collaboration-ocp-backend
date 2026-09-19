package com.ocp.project_management_service.dto.request;

import com.ocp.project_management_service.entity.Project;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ProjectRequestDTO {

    @NotBlank(message = "le nom est obligatoire")
    private String name ;

    @NotBlank(message = "la description est obligatoire")
    private String description;

    @NotNull(message = "affectation responsible est obligatoire")
    private UUID responsibleId;

    @NotNull(message = "department est obligatoire")
    private UUID departmentId;

    @NotNull
    private LocalDate startDate;

    @NotNull(message = "la date de fin obligatoire")
    private LocalDate endDate ;
    @NotNull(message = "La priorité est obligatoire")
    private Project.ProjectPriority projectPriority; // LOW, MEDIUM, HIGH, CRITICAL
    private BigDecimal budget;

}
