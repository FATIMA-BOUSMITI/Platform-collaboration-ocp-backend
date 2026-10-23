package com.ocp.auth_service.dto.response;

import java.util.UUID;

public record UserRoleAssignmentResponse(
    UUID userId,
    UUID roleId,
    String roleName,
    String roleDescription
) {
}
