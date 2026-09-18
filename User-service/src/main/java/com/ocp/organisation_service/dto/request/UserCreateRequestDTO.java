package com.ocp.organisation_service.dto.request;

import com.ocp.organisation_service.dto.response.RoleResponseDTO;
import lombok.Data;

import java.util.UUID;

@Data
public class UserCreateRequestDTO {


	private UUID id;
	// id venant de Auth-Service


	private String firstName;


	private String lastName;
	private String email;


	private String phone;


	private String photoUrl;


	private String position;


	private UUID managerId;


	private String language;


	private String timezone;
	private UUID departement;
	private UUID roleId;

}
