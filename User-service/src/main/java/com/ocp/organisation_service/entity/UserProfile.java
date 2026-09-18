package com.ocp.organisation_service.entity;

import com.ocp.organisation_service.dto.response.DepartementResponseDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Table(name="user_profiles")
@Data
public class UserProfile {


	@Id
	private UUID id;


	private String firstName;


	private String lastName;
	private String email;
	private UUID authUserId;


	private String phone;


	private String photoUrl;


	private String position;


	private UUID managerId;


	private String language;


	private String timezone;
	@ManyToOne
	@JoinColumn(name = "departement_id")
	private Departement departement;


}
