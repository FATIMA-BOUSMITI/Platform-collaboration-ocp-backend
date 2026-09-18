package com.ocp.organisation_service.dto.response;

import  com.ocp.organisation_service.entity.Departement;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserResponseDTO {


	private UUID id;
	private UUID userId;


	private String firstName;


	private String lastName;
	private String email;


	private String phone;


	private String photoUrl;


	private String position;


	private UUID managerId;


	private String language;


	private String timezone;
	// vient du Auth Service
	private List<RoleResponseDTO> roles;
	private DepartementResponseDTO departement;


}
