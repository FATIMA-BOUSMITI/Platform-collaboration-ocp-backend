package com.ocp.project_management_service.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;


import java.math.BigDecimal;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "project")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Project {

    public enum  ProjectStatus{

        PLANNED ,
        IN_PROGRESS ,
        ON_HOLD ,
        COMPLETED ,
        ARCHIVED

    };

    public enum  ProjectPriority{

        LOW,
        MEDIUM ,
        HIGH,
        CRITICAL

    };

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    @Column(nullable = false,length = 50)
    private String reference ;

    @Column(nullable = false,length = 150 )
    private String name ;

    @Column(nullable = false)
    private String description;

    @Column(name="responsible_id",nullable = false)
    private UUID responsibleId;

    @Column(name="department_id",nullable = false)
    private UUID departmentId;

    @CreationTimestamp
    @Column(name="start_date")
    private LocalDate startDate ;

    @Column(name="end_date")
    private LocalDate endDate ;

    @Column(name="project_priority",nullable = false)
    @Enumerated(EnumType.STRING)
    private  ProjectPriority projectPriority;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private  ProjectStatus status;

    @Column(nullable = false)
    private float progress ;

    @Column(nullable = false)
    private BigDecimal budget ;

    @OneToMany(mappedBy = "project",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Task> tasks= new ArrayList<>();

    @OneToMany(mappedBy = "project",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<CalendarEvent> calendarEvents= new ArrayList<>();





}
