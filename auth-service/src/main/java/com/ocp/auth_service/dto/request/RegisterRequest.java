package com.ocp.auth_service.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor


public class RegisterRequest {

	private UUID userId;     // créé par le User Service ou reçu de celui-ci

	private String email;

	private String password;
}
