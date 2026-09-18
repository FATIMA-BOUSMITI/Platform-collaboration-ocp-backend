package com.ocp.organisation_service.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class RoleResponseDTO {

	private UUID id;

	private String name;

	private String description;
}
