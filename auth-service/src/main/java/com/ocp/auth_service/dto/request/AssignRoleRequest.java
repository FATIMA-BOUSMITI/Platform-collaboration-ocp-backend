package com.ocp.auth_service.dto.request;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


import java.util.Set;
import java.util.UUID;

@Getter
@Setter

public class AssignRoleRequest {
    @NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    private UUID userId;

    @NotEmpty(message = "Au moins un rôle doit être fourni")
    private Set<UUID> roleIds;
}
