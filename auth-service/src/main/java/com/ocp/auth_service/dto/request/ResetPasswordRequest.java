package com.ocp.auth_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest {
	private String newPassword;
	private String confirmedPassword;
	private String token;
}
