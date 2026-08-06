package com.ocp.organisation_service.clients.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class AuthUserResponse {

	private UUID id;
	private String email;
	private boolean enabled;
}
