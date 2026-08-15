package com.ocp.project_management_service.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
public class CalendarEventDTO {

    private UUID id;
    private String title;
    private String type;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
