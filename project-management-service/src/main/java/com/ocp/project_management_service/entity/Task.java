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
@lombok.NoArgsConstructor
@lombok.AllArgsConstructor
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
    private UUID columnId;
    private long sortIndex;
    @Version private long version;


    @Column(nullable = false,length = 150 )
    private String title ;

    @Column(nullable = false)
    private String description;

    @Column(name="assignee_id")
    private UUID assigneeId;
    @Column(name="due_date")
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
    @Builder.Default
    private List<SubTask> subTasks = new ArrayList<>();

    @OneToMany(mappedBy = "task",fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @Builder.Default
    private List<TaskComment> taskComments = new ArrayList<>();


}
