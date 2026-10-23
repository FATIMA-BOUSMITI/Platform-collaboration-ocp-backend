package com.ocp.communication_service.controller;

import com.ocp.communication_service.dto.response.ErrorResponse;
import com.ocp.communication_service.exception.ConversationNotFoundException;
import com.ocp.communication_service.exception.NotConversationMemberException;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CommunicationExceptionHandler {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(CommunicationExceptionHandler.class);

    @ExceptionHandler({AccessDeniedException.class, NotConversationMemberException.class})
    public ResponseEntity<ErrorResponse> forbidden(
            RuntimeException exception, HttpServletRequest request) {
        LOGGER.warn("Forbidden conversation operation at {}", request.getRequestURI());
        return error(HttpStatus.FORBIDDEN, exception, request);
    }

    @ExceptionHandler(ConversationNotFoundException.class)
    public ResponseEntity<ErrorResponse> notFound(
            ConversationNotFoundException exception, HttpServletRequest request) {
        return error(HttpStatus.NOT_FOUND, exception, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> invalid(IllegalArgumentException exception,HttpServletRequest request){return error(HttpStatus.BAD_REQUEST,exception,request);}
    @ExceptionHandler(org.springframework.web.server.ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> status(org.springframework.web.server.ResponseStatusException exception,HttpServletRequest request){return error(HttpStatus.valueOf(exception.getStatusCode().value()),exception,request);}
    @ExceptionHandler(org.springframework.web.multipart.MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> tooLarge(org.springframework.web.multipart.MaxUploadSizeExceededException exception,HttpServletRequest request){return error(HttpStatus.PAYLOAD_TOO_LARGE,exception,request);}
    @ExceptionHandler(org.springframework.orm.ObjectOptimisticLockingFailureException.class)
    public ResponseEntity<ErrorResponse> conflict(org.springframework.orm.ObjectOptimisticLockingFailureException exception,HttpServletRequest request){return error(HttpStatus.CONFLICT,exception,request);}
    private ResponseEntity<ErrorResponse> error(
            HttpStatus status, RuntimeException exception, HttpServletRequest request) {
        ErrorResponse response = ErrorResponse.builder()
            .timestamp(LocalDateTime.now())
            .status(status.value())
            .error(status.getReasonPhrase())
            .message(exception.getMessage())
            .path(request.getRequestURI())
            .build();
        return ResponseEntity.status(status).body(response);
    }
}
