package com.ocp.auth_service.exception;

public class UserDeletionNotAllowedException extends RuntimeException {
	public UserDeletionNotAllowedException(String message) {
		super(message);
	}
}
