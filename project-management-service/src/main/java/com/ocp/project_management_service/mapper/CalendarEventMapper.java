package com.ocp.project_management_service.mapper;

import com.ocp.project_management_service.dto.request.CalendarEventRequestDTO;
import com.ocp.project_management_service.dto.response.CalendarEventDTO;
import com.ocp.project_management_service.entity.CalendarEvent;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface CalendarEventMapper {
    CalendarEvent toEntity(CalendarEventRequestDTO request);
    CalendarEventDTO toResponse(CalendarEvent calendarEvent);

}
