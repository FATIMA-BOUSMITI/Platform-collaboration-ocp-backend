package com.ocp.auth_service.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UpdateRolePermissionsRequest {

    @NotNull(message = "La liste des permissions est obligatoire")
    private Set<UUID> permissionIds;
}
