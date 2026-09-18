package com.ocp.organisation_service.dto.request;

import com.ocp.organisation_service.enums.DepartmentStatus;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Builder

@Data
public class DepartementRequestDTO {

	private String name;

	private String code;

	private String description;

	private UUID managerId;

	private DepartmentStatus status;
}
