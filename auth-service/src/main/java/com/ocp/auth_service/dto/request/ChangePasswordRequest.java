package com.ocp.auth_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
	private String newPassword;
	private String currentPassword;

}
