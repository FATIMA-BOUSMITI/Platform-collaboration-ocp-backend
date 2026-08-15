package com.ocp.project_management_service.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name="calendar_event")
@Getter
@Setter
@Builder
public class CalendarEvent {


    public enum CalendarEventType{
        MEETING ,
        DEADLINE,
        LEAVE

    };

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;


    @Column(nullable = false,length = 150 )
    private String title ;

    @Enumerated(EnumType.STRING)
    private CalendarEventType calendarEventType;

    @Column(name="start_at",nullable = false)
    private LocalDateTime startAt;

    @Column(name="end_at",nullable = false)
    private LocalDateTime endAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id",nullable = false)
    private Project  project ;

}
