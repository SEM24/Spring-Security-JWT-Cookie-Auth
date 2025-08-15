package com.security.fiverr.exception;

import com.security.fiverr.exception.dto.ErrorMessage;
import com.security.fiverr.exception.dto.ErrorMessageResponse;
import com.security.fiverr.exception.service.PropertiesMessageService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@RestControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class ValidationHandler {
    private final PropertiesMessageService propertiesMessageService;

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<?> handleConstraintViolationException(ConstraintViolationException e) {
        log.error("ConstraintViolationException: {}", e.getMessage(), e);
        HttpStatus status = BAD_REQUEST;
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();

        ErrorMessageResponse response = getErrorMessageResponse(e, status);
        List<ErrorMessage> additionalMessages = violations.stream()
                .map(violation -> ErrorMessage.builder()
                        .message(getMessage(violation.getMessage()))
                        .field(violation.getPropertyPath().toString())
                        .invalidValue(violation.getInvalidValue())
                        .build()
                ).toList();
        response.details().put("errors", additionalMessages);

        return ResponseEntity.status(status).body(response);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("MethodArgumentNotValidException: {}", e.getMessage(), e);
        HttpStatus status = BAD_REQUEST;
        ErrorMessageResponse response = getErrorMessageResponse(e, status);

        List<ErrorMessage> fieldErrors = e.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> ErrorMessage.builder()
                        .message(getMessage(fieldError.getDefaultMessage()))
                        .field(fieldError.getField())
                        .invalidValue(fieldError.getRejectedValue())
                        .build()
                ).toList();

        List<ErrorMessage> globalErrors = e.getBindingResult().getGlobalErrors().stream()
                .map(globalError -> ErrorMessage.builder()
                        .message(getMessage(globalError.getDefaultMessage()))
                        .field("global")
                        .invalidValue(null)
                        .build()
                ).toList();

        response.details().put("fieldErrors", fieldErrors);
        response.details().put("globalErrors", globalErrors);

        return ResponseEntity.status(status).body(response);
    }

    private ErrorMessageResponse getErrorMessageResponse(Throwable e, HttpStatus status) {
        return ErrorMessageResponse.builder()
                .type(MessageType.VALIDATION_ERROR)
                .createdAt(Instant.now())
                .statusCode(status)
                .status(status.value())
                .message("Validation failed")
                .details(new HashMap<>())
                .build();
    }

    private String getMessage(String message) {
        if (message == null) {
            return "Validation error";
        }
        String result = propertiesMessageService.getProperty(message);
        return result != null ? result : message;
    }
}