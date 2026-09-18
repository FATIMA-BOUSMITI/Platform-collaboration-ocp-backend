package com.ocp.organisation_service.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="teams")
@Data
public class Team {


	@Id
	private UUID id;


	private String name;


	@ManyToOne
	@JoinColumn(name="department_id")
	private Departement department;


	private UUID responsableId;


}
