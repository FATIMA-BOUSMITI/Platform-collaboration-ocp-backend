package com.ocp.organisation_service.dto.request;

import lombok.Data;

import java.util.Set;
import java.util.UUID;

@Data
public class CreateCredentialRequest {

	private UUID userId;

	private String email;
	private UUID roleId;


}
