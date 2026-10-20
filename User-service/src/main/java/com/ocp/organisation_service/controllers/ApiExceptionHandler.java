package com.ocp.organisation_service.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiExceptionHandler.class);

	@ExceptionHandler(ResourceAccessException.class)
	public ResponseEntity<Map<String, Object>> handleDownstreamTransportFailure(
		ResourceAccessException exception,
		HttpServletRequest request) {
		LOGGER.warn(
			"Authentication service request timed out or could not connect while handling {}",
			request.getRequestURI(),
			exception);
		return errorResponse(
			HttpStatus.GATEWAY_TIMEOUT,
			"Authentication service did not respond in time.",
			request);
	}

	@ExceptionHandler(RestClientResponseException.class)
	public ResponseEntity<Map<String, Object>> handleDownstreamFailure(
		RestClientResponseException exception,
		HttpServletRequest request) {
		HttpStatus downstreamStatus = HttpStatus.valueOf(exception.getStatusCode().value());
		HttpStatus responseStatus = downstreamStatus.is4xxClientError()
			? downstreamStatus
			: HttpStatus.BAD_GATEWAY;

		if (downstreamStatus == HttpStatus.CONFLICT) {
			LOGGER.warn(
				"Authentication service returned HTTP 409 while handling {}",
				request.getRequestURI());
		} else {
			LOGGER.warn(
				"Authentication service returned HTTP {} while handling {}",
				downstreamStatus.value(),
				request.getRequestURI());
		}

		return errorResponse(
			responseStatus,
			"Authentication service rejected the user operation.",
			request);
	}

	@ExceptionHandler(DataIntegrityViolationException.class)
	public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(
		DataIntegrityViolationException exception,
		HttpServletRequest request) {
		LOGGER.warn("Database integrity constraint rejected request {}", request.getRequestURI(), exception);
		return errorResponse(
			HttpStatus.CONFLICT,
			"The user cannot be deleted while related records still reference it.",
			request);
	}

	@ExceptionHandler(IllegalArgumentException.class)
 public ResponseEntity<Map<String,Object>> invalid(IllegalArgumentException e,HttpServletRequest r){return errorResponse(HttpStatus.BAD_REQUEST,e.getMessage(),r);}

 private ResponseEntity<Map<String, Object>> errorResponse(
		HttpStatus status,
		String message,
		HttpServletRequest request) {
		return ResponseEntity.status(status).body(Map.of(
			"timestamp", LocalDateTime.now().toString(),
			"status", status.value(),
			"error", status.getReasonPhrase(),
			"message", message,
			"path", request.getRequestURI()));
	}
}
