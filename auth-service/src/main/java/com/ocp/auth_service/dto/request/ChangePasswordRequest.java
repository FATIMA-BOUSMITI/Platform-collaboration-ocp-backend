package com.ocp.auth_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ChangePasswordRequest {
	@jakarta.validation.constraints.NotBlank
 @jakarta.validation.constraints.Size(min=10,max=128)
 private String newPassword;
	@jakarta.validation.constraints.NotBlank
 private String currentPassword;

}
