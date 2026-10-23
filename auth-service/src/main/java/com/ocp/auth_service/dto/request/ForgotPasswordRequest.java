package com.ocp.auth_service.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest {
	@NotBlank(message = "L'adresse e-mail est obligatoire.")
	@Email(message = "L'adresse e-mail est invalide.")
	private String email;
}
