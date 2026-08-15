package com.ocp.project_management_service.dto.response;


import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ProjectResponseDTO {
    private UUID id;
    private String reference;
    private String name;
    private String description;
    private UUID responsibleId;
    private UUID departmentId;
    private LocalDate startDate;
    private LocalDate endDate;
    private String priority;
    private String status;
    private float progress;
    private BigDecimal budget;
    private List<TaskResponseDTO> tasks ;
    private List<CalendarEventDTO> calendarEvents;
}
