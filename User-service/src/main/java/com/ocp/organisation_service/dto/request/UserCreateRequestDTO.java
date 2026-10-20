package com.ocp.organisation_service.dto.request;

import com.ocp.organisation_service.dto.response.RoleResponseDTO;
import lombok.Data;

import java.util.UUID;

@Data
public class UserCreateRequestDTO {


	private UUID id;
	// id venant de Auth-Service


	@jakarta.validation.constraints.NotBlank
 @jakarta.validation.constraints.Size(min=1,max=150)
 private String firstName;


	@jakarta.validation.constraints.Size(min=1,max=150)
 private String lastName;
	@jakarta.validation.constraints.NotBlank
 @jakarta.validation.constraints.Email
 private String email;


	private String phone;


	private String photoUrl;


	private String position;


	private UUID managerId;


	private String language;


	private String timezone;
	@jakarta.validation.constraints.NotNull
 private UUID departement;
	@jakarta.validation.constraints.NotNull
 private UUID roleId;

}
