package com.ocp.auth_service.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
	@NotBlank(message = "Le nouveau mot de passe est obligatoire.")
	@Size(min = 8, max = 72, message = "Le mot de passe doit contenir entre 8 et 72 caractères.")
	private String newPassword;

	@NotBlank(message = "La confirmation du mot de passe est obligatoire.")
	private String confirmedPassword;

	@NotBlank(message = "Le jeton de réinitialisation est obligatoire.")
	private String token;
}
