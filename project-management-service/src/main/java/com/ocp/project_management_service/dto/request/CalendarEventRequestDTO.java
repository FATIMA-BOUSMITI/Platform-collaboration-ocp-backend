package com.ocp.project_management_service.dto.request;

import com.ocp.project_management_service.entity.CalendarEvent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CalendarEventRequestDTO {

    @NotBlank
    private String title;
    @NotNull
    private CalendarEvent.CalendarEventType type; // MEETING, DEADLINE, LEAVE
    @NotNull
    private LocalDateTime startAt;
    @NotNull
    private LocalDateTime endAt;
}
