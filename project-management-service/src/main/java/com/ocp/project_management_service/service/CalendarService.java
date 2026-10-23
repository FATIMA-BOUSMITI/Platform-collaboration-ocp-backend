package com.ocp.project_management_service.service;

import com.ocp.project_management_service.dto.request.CalendarEventRequestDTO;
import com.ocp.project_management_service.dto.response.CalendarEventDTO;
import com.ocp.project_management_service.entity.CalendarEvent;
import com.ocp.project_management_service.entity.Project;
import com.ocp.project_management_service.exception.CalendarEventNotFoundException;
import com.ocp.project_management_service.exception.ProjectNotFoundException;
import com.ocp.project_management_service.mapper.CalendarEventMapper;
import com.ocp.project_management_service.repository.CalendarEventRepository;
import com.ocp.project_management_service.repository.ProjectRepository;
import com.ocp.project_management_service.security.AuthenticatedUser;
import com.ocp.project_management_service.security.ProjectAccessPolicy;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CalendarService {
    private final CalendarEventRepository calendarEventRepository;
    private final ProjectRepository projectRepository ;
    private final CalendarEventMapper calendarEventMapper ;
    private final ProjectAccessPolicy projectAccessPolicy;

    @Transactional
    public CalendarEventDTO createEvent(
            UUID projectId, CalendarEventRequestDTO request,
            AuthenticatedUser authenticatedUser){
        Project project = projectRepository.findById(projectId).orElseThrow(
                ()-> new ProjectNotFoundException(projectId.toString())
        );
        projectAccessPolicy.requireProjectManagement(project, authenticatedUser);
        CalendarEvent calendarEvent = CalendarEvent.builder()
                .title(request.getTitle())
                .calendarEventType(request.getType())
                .startAt(request.getStartAt())
                .endAt(request.getEndAt())
                .project(project)
                .build();
        CalendarEvent calendarEvent1=calendarEventRepository.save(calendarEvent);
        return calendarEventMapper.toResponse(calendarEvent1);
    }

    @Transactional(readOnly = true)
    public List<CalendarEventDTO> getProjectCalendar(
            UUID projectId, AuthenticatedUser authenticatedUser){

        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ProjectNotFoundException(projectId.toString()));
        projectAccessPolicy.requireProjectRead(project, authenticatedUser);

        return calendarEventRepository.findByProjectIdOrderByStartAtAsc(projectId)
                .stream()
                .map(calendarEventMapper::toResponse).
                collect(Collectors.toList());
    }

    @Transactional
    public void deleteEvent(UUID eventId, AuthenticatedUser authenticatedUser){
        CalendarEvent event = calendarEventRepository.findById(eventId)
                .orElseThrow(() -> new CalendarEventNotFoundException(eventId.toString()));
        projectAccessPolicy.requireProjectManagement(event.getProject(), authenticatedUser);
        calendarEventRepository.delete(event);
    }

}
