package com.ocp.project_management_service.repository;

import com.ocp.project_management_service.entity.CalendarEvent;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CalendarEventRepository extends JpaRepository<CalendarEvent, UUID> {
    List<CalendarEvent> findByProjectIdOrderByStartAtAsc(UUID projectId);
}
