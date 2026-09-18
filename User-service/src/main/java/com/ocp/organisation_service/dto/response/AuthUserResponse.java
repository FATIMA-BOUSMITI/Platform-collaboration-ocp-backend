package com.ocp.organisation_service.dto.response;



import lombok.Data;

import java.util.UUID;

@Data
public class AuthUserResponse {

	private UUID userId;

	private String email;

	private Boolean enabled;

}
