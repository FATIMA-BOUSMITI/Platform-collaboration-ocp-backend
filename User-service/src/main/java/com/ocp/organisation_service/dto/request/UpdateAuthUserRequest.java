package com.ocp.organisation_service.dto.request;

import lombok.Data;

import java.util.UUID;

@Data
public class UpdateAuthUserRequest {

	private String email;
	private Boolean enabled;
	private Boolean accountLocked;
	private UUID roleId;
}
