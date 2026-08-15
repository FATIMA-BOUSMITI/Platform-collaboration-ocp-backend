package com.ocp.project_management_service.controller;

import com.ocp.project_management_service.dto.request.CalendarEventRequestDTO;
import com.ocp.project_management_service.dto.response.CalendarEventDTO;
import com.ocp.project_management_service.service.CalendarService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api")
@RestController
@AllArgsConstructor
public class CalendarController {
    private final CalendarService calendarService;

   @PostMapping("/projects/{projectId}/calendar")
    public ResponseEntity<CalendarEventDTO> createCalendar( @PathVariable UUID projectId ,
                                                            @Valid @RequestBody  CalendarEventRequestDTO request ){

        CalendarEventDTO response = calendarService.createEvent(projectId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);

   }
   @GetMapping("/projects/{projectId}/calendar")
    public ResponseEntity<List<CalendarEventDTO>> getCalendar(@PathVariable UUID projectId){

       List<CalendarEventDTO> response = calendarService.getProjectCalendar(projectId);
       return ResponseEntity.ok(response);
   }

   @DeleteMapping("/calendar/{eventId}")
    public ResponseEntity<Void> deleteCalendar(@PathVariable UUID eventId){
       calendarService.deleteEvent(eventId);
       return ResponseEntity.noContent().build();

   }

}
