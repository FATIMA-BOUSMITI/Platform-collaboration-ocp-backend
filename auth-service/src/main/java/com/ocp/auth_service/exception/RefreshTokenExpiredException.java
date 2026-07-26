package com.ocp.auth_service.exception;

public class RefreshTokenExpiredException extends RuntimeException {

	public RefreshTokenExpiredException(String message) {
		super(message);
	}
}
