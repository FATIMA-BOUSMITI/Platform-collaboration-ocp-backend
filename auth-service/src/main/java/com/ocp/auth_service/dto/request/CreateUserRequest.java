package com.ocp.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "L'email est obligatoire ")
    @Email(message = "Format d'email invalide")
    private String email ;

    @NotNull(message = "L'identifiant de l'utilisateur est obligatoire")
    private UUID userId;

    @NotNull(message = "L'identifiant du rôle est obligatoire")
    private UUID roleId;
}
