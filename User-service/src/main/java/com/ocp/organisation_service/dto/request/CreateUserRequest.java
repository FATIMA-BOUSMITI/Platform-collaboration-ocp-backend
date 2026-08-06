package com.ocp.organisation_service.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;


@Getter
@Setter
public class CreateUserRequest {


	@NotBlank(message = "First name required")
	private String firstName;


	@NotBlank(message = "Last name required")
	private String lastName;


	@Email(message = "Invalid email")
	@NotBlank(message = "Email required")
	private String email;


	private String phone;


	private String jobTitle;


	private UUID departmentId;


}
