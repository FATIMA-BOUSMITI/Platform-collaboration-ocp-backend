package com.ocp.organisation_service.dto.response;

import java.util.UUID;

public record AuthUserRoleAssignmentResponse(
	UUID userId,
	UUID roleId,
	String roleName,
	String roleDescription
) {
}
