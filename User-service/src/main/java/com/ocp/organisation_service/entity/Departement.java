package com.ocp.organisation_service.entity;

import com.ocp.organisation_service.enums.DepartmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "departments")
public class Departement {

	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private UUID id;

	@Column(nullable = false)
	private String name;

	@Column(unique = true)
	private String code;

	@Column(length = 500)
	private String description;

	private UUID managerId;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private DepartmentStatus status = DepartmentStatus.ACTIVE;

	@OneToMany(mappedBy = "department")
	private List<Team> teams = new ArrayList<>();

	@OneToMany(mappedBy = "departement")
	private List<UserProfile> users = new ArrayList<>();
}
