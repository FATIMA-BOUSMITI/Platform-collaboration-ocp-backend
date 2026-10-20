package com.ocp.auth_service.dto.request;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AssignRoleRequest {

    @NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    private UUID userId;

    @NotNull(message = "L'identifiant du rôle est obligatoire")
    private UUID roleId;
}
