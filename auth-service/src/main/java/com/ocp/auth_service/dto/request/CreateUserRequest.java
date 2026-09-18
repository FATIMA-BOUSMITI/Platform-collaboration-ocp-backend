package com.ocp.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class CreateUserRequest {

    @NotBlank(message = "L'email est obligatoire ")
    @Email(message = "Fromat d'email invalide")
    private String email ;
	private UUID userId;
	private UUID roleId;




}
