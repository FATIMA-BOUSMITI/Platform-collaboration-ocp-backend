package com.ocp.auth_service.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

	private ResponseEntity<ErrorResponse> build(
		HttpStatus status,
		String message,
		HttpServletRequest request
	) {

		ErrorResponse error = ErrorResponse.builder()
			.timestamp(LocalDateTime.now())
			.status(status.value())
			.error(status.getReasonPhrase())
			.message(message)
			.path(request.getRequestURI())
			.build();

		return ResponseEntity.status(status).body(error);
	}

	// ===========================
	// User
	// ===========================

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(
		UserNotFoundException ex,
		HttpServletRequest request) {

		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	@ExceptionHandler(EmailAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleEmailAlreadyExists(
		EmailAlreadyExistsException ex,
		HttpServletRequest request) {

		return build(HttpStatus.CONFLICT, ex.getMessage(), request);
	}

	// ===========================
	// Authentication
	// ===========================

	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(
		InvalidCredentialsException ex,
		HttpServletRequest request) {

		return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
	}

	@ExceptionHandler(AccountLockedException.class)
	public ResponseEntity<ErrorResponse> handleAccountLocked(
		AccountLockedException ex,
		HttpServletRequest request) {

		return build(HttpStatus.LOCKED, ex.getMessage(), request);
	}

	@ExceptionHandler(AccountDisabledException.class)
	public ResponseEntity<ErrorResponse> handleAccountDisabled(
		AccountDisabledException ex,
		HttpServletRequest request) {

		return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
	}

	// ===========================
	// Refresh Token
	// ===========================

	@ExceptionHandler(InvalidRefreshTokenException.class)
	public ResponseEntity<ErrorResponse> handleInvalidRefreshToken(
		InvalidRefreshTokenException ex,
		HttpServletRequest request) {

		return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
	}

	@ExceptionHandler(RefreshTokenExpiredException.class)
	public ResponseEntity<ErrorResponse> handleRefreshTokenExpired(
		RefreshTokenExpiredException ex,
		HttpServletRequest request) {

		return build(HttpStatus.UNAUTHORIZED, ex.getMessage(), request);
	}

	// ===========================
	// Role
	// ===========================

	@ExceptionHandler(RoleNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleRoleNotFound(
		RoleNotFoundException ex,
		HttpServletRequest request) {

		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	// ===========================
	// Permission
	// ===========================

	@ExceptionHandler(PermissionNotFoundException.class)
	public ResponseEntity<ErrorResponse> handlePermissionNotFound(
		PermissionNotFoundException ex,
		HttpServletRequest request) {

		return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
	}

	// ===========================
	// Validation
	// ===========================

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidation(
		MethodArgumentNotValidException ex,
		HttpServletRequest request) {

		String message = ex.getBindingResult()
			.getFieldErrors()
			.stream()
			.map(FieldError::getDefaultMessage)
			.collect(Collectors.joining(", "));

		return build(HttpStatus.BAD_REQUEST, message, request);
	}

	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<ErrorResponse> handleTypeMismatch(
		MethodArgumentTypeMismatchException ex,
		HttpServletRequest request) {

		return build(
			HttpStatus.BAD_REQUEST,
			"Paramètre invalide : " + ex.getName(),
			request
		);
	}

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<ErrorResponse> handleInvalidJson(
		HttpMessageNotReadableException ex,
		HttpServletRequest request) {

		return build(
			HttpStatus.BAD_REQUEST,
			"Le corps de la requête est invalide.",
			request
		);
	}

	// ===========================
	// Spring Security
	// ===========================

	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(
		AccessDeniedException ex,
		HttpServletRequest request) {

		return build(
			HttpStatus.FORBIDDEN,
			"Accès refusé.",
			request
		);
	}

	// ===========================
	// Runtime
	// ===========================

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntime(
		RuntimeException ex,
		HttpServletRequest request) {

		return build(
			HttpStatus.BAD_REQUEST,
			ex.getMessage(),
			request
		);
	}

	// ===========================
	// Other Exceptions
	// ===========================

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleOther(
		Exception ex,
		HttpServletRequest request) {

		ex.printStackTrace();

		return build(
			HttpStatus.INTERNAL_SERVER_ERROR,
			"Une erreur interne est survenue.",
			request
		);
	}
}
