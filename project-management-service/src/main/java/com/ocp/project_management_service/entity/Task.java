package com.ocp.project_management_service.entity;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Generated;
import lombok.Getter;
import lombok.Setter;

import java.time.Duration;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name="task")
@Getter
@Setter
@Builder
public class Task {
    public  enum TaskStatus{
        TODO,
        IN_PROGRESS ,
        REVIEW,
        DONE ,
    ARCHIVED
    };

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;


    @Column(nullable = false,length = 150 )
    private String title ;

    @Column(nullable = false)
    private String description;

    @Column(name="assignee_id",nullable = false)
    private UUID assigneeId;
    @Column(name="due_date",nullable = false)
    private LocalDate dueDate ;

    @Column(name="estimated_time",nullable = false)
    private Duration estimatedTime;

    @Column(name="real_time",nullable = false)
    private Duration realTime;

    @Enumerated(EnumType.STRING)
    private TaskStatus status ;

    @Enumerated(EnumType.STRING)
    private Project.ProjectPriority priority ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="project_id",nullable = false)
    private Project project ;

    @OneToMany(mappedBy = "task",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<SubTask> subTasks = new ArrayList<>();

    @OneToMany(mappedBy = "task",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<TaskComment> taskComments = new ArrayList<>();


}
