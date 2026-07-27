package com.ocp.auth_service.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

import lombok.Setter;
import java.lang.String;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter

@AllArgsConstructor
@Builder
public class RefreshTokenResponse {

	private String accessToken;

	private String refreshToken;

	private Date expiryDate;
}
