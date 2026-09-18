package com.ocp.organisation_service.dto.response;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class DepartementResponseDTO {
	private UUID id;
	private String name;
}
