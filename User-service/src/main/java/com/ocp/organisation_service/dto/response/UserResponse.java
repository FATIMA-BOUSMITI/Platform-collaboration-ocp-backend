package com.ocp.organisation_service.dto.response;

import com.ocp.organisation_service.enums.UserStatus;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class UserResponse {


	private UUID id;

	private String firstName;

	private String lastName;

	private String email;

	private String phone;

	private String jobTitle;

	private UserStatus status;

	private UUID departmentId;

	private UUID authUserId;

}
