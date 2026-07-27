package com.ocp.auth_service.dto.request;


import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;
@Setter
@Getter

public class UpdateRoleRequest {
	private String name;
	private String description;
	private Set<UUID> permissionIds;
}
