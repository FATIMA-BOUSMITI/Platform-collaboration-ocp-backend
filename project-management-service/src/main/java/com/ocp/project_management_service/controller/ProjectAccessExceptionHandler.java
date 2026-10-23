package com.ocp.project_management_service.controller;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ProjectAccessExceptionHandler {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(ProjectAccessExceptionHandler.class);

    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<Map<String,Object>> conflict(){return ResponseEntity.status(409).body(Map.of("status",409,"message","La tâche a été modifiée. Actualisez le tableau."));}
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String,Object>> invalid(IllegalArgumentException e){return ResponseEntity.badRequest().body(Map.of("status",400,"message",String.valueOf(e.getMessage())));}
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> forbidden(
            AccessDeniedException exception, HttpServletRequest request) {
        LOGGER.warn("Forbidden project/task operation at {}", request.getRequestURI());
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
            "timestamp", Instant.now().toString(),
            "status", HttpStatus.FORBIDDEN.value(),
            "error", HttpStatus.FORBIDDEN.getReasonPhrase(),
            "message", exception.getMessage(),
            "path", request.getRequestURI()));
    }
}
