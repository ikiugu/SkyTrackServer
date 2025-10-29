package com.ikiugu.skytrackserver.config;

import com.ikiugu.skytrackserver.core.exception.ApiException;
import com.ikiugu.skytrackserver.core.exception.AuthenticationException;
import com.ikiugu.skytrackserver.core.exception.ResourceNotFoundException;
import com.ikiugu.skytrackserver.core.exception.ValidationException;
import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    logger.warn("Resource not found: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), ex.getStatus());
  }

  @ExceptionHandler(ValidationException.class)
  public ResponseEntity<Map<String, Object>> handleValidationException(ValidationException ex) {
    logger.warn("Validation error: {}", ex.getMessage());
    Map<String, Object> body = new HashMap<>();
    body.put("error", ex.getMessage());
    body.put("errors", ex.getErrors());
    body.put("status", ex.getStatus().value());
    return new ResponseEntity<>(body, ex.getStatus());
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<Map<String, Object>> handleAuthenticationException(
      AuthenticationException ex) {
    logger.warn("Authentication error: {}", ex.getMessage());
    return buildErrorResponse(ex.getMessage(), ex.getStatus());
  }

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<Map<String, Object>> handleApiException(ApiException ex) {
    logger.error("API error: {}", ex.getMessage(), ex);
    return buildErrorResponse(ex.getMessage(), ex.getStatus());
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, Object>> handleConstraintViolationException(
      ConstraintViolationException ex) {
    logger.warn("Constraint violation: {}", ex.getMessage());
    Map<String, Object> body = new HashMap<>();
    body.put("error", "Validation failed");
    body.put(
        "errors",
        ex.getConstraintViolations().stream()
            .map(v -> v.getPropertyPath() + ": " + v.getMessage())
            .collect(Collectors.toList()));
    body.put("status", HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(DataAccessException.class)
  public ResponseEntity<Map<String, Object>> handleDataAccessException(DataAccessException ex) {
    logger.error("Database error: {}", ex.getMessage(), ex);
    return buildErrorResponse("Database operation failed", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request) {
    logger.warn("Method argument validation failed: {}", ex.getMessage());
    Map<String, Object> body = new HashMap<>();
    body.put("error", "Validation failed");
    body.put(
        "errors",
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .collect(Collectors.toList()));
    body.put("status", HttpStatus.BAD_REQUEST.value());
    return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
    logger.error("Unexpected error: {}", ex.getMessage(), ex);
    return buildErrorResponse("An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<Map<String, Object>> buildErrorResponse(
      String message, HttpStatus status) {
    Map<String, Object> body = new HashMap<>();
    body.put("error", message);
    body.put("status", status.value());
    return new ResponseEntity<>(body, status);
  }
}
