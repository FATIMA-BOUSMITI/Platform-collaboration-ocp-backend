package com.ocp.auth_service.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name="permissions")
@Getter
@Setter

public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id ;

    @Column(nullable = false,unique = true,length = 50)
    private String name ;

    @Column(length=255)
    private String description ;

}
