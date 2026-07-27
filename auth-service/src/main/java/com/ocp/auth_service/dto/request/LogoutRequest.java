package com.ocp.auth_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class LogoutRequest {
	private String refreshToken;
}
