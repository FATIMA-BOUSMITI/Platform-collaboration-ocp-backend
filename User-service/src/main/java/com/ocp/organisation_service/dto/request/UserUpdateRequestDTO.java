package com.ocp.organisation_service.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UserUpdateRequestDTO {

	// UserProfile
	@jakarta.validation.constraints.Size(min=1,max=150)
 private String firstName;
	@jakarta.validation.constraints.Size(min=1,max=150)
 private String lastName;
	@jakarta.validation.constraints.Email
 private String email;
	private String phone;
	private String photoUrl;
	private String position;
	private UUID managerId;
	private String language;
	private String timezone;
	private UUID departement;

	// Auth-Service
	private UUID roleId;
}
