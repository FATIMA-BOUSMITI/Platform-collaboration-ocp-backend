package com.ocp.project_management_service.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;
@Entity
@Table(name="sub_task")
@Getter
@Setter
@Builder
public class SubTask {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;


    @Column(nullable = false,length = 150 )
    private String title ;

    @Column(name="is_done",nullable = false)
    private boolean isDone;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="task_id",nullable = false)
    private Task task ;

}
