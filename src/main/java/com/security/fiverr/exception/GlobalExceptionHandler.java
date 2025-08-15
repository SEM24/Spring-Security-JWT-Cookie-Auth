package com.security.fiverr.exception;

import com.security.fiverr.exception.dto.ErrorMessageResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalServiceException.class)
    public ResponseEntity<?> handleResponseStatusException(GlobalServiceException e) {
        log.error("GlobalServiceException: {}", e.getMessage(), e);
        return createDefaultErrorResponse(e);
    }

    @ExceptionHandler(PropertyReferenceException.class)
    public ResponseEntity<?> handlePropertyReferenceException(PropertyReferenceException e) {
        log.error("PropertyReferenceException: {}", e.getMessage(), e);
        return createErrorResponse(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsException(BadCredentialsException e) {
        log.error("BadCredentialsException in controller: {}", e.getMessage(), e);
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(AccessDeniedException e) {
        log.error("AccessDeniedException in controller: {}", e.getMessage(), e);
        return createErrorResponse(HttpStatus.FORBIDDEN, "Access denied");
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(AuthenticationException e) {
        log.error("AuthenticationException in controller: {}", e.getMessage(), e);
        return createErrorResponse(HttpStatus.UNAUTHORIZED, "Authentication failed");
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleGenericException(Exception e) {
//        log.error("Unexpected exception: {}", e.getMessage(), e);
//        return createErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
//    }

    private ResponseEntity<?> createErrorResponse(HttpStatus httpStatus, String message) {
        Map<String, Object> responseBody = new LinkedHashMap<>();
        responseBody.put("timestamp", LocalDateTime.now());
        responseBody.put("status", httpStatus.value());
        responseBody.put("error", httpStatus.getReasonPhrase());
        responseBody.put("message", message);
        return new ResponseEntity<>(responseBody, httpStatus);
    }

    private ResponseEntity<?> createDefaultErrorResponse(ResponseStatusException e) {
        return ResponseEntity.status(HttpStatus.valueOf(e.getStatusCode().value()))
                .body(getErrorMessageBody(e));
    }

    private ErrorMessageResponse getErrorMessageBody(ResponseStatusException e) {
        HttpStatus status = HttpStatus.valueOf(e.getStatusCode().value());
        return ErrorMessageResponse.builder()
                .message(e.getReason())
                .createdAt(Instant.now())
                .statusCode(status)
                .status(status.value())
                .type(MessageType.ERROR)
                .build();
    }
}