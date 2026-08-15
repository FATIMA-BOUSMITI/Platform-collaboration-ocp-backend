package com.ocp.organisation_service.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class UpdateUserRequest {


	private String firstName;

	private String lastName;

	private String phone;

	private String jobTitle;

	private UUID departmentId;

}
