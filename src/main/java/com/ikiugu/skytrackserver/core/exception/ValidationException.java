package com.ikiugu.skytrackserver.core.exception;

import java.util.List;
import org.springframework.http.HttpStatus;

public class ValidationException extends ApiException {
  private final List<String> errors;

  public ValidationException(String message) {
    super(message, HttpStatus.BAD_REQUEST);
    this.errors = List.of(message);
  }

  public ValidationException(List<String> errors) {
    super("Validation failed", HttpStatus.BAD_REQUEST);
    this.errors = errors;
  }

  public List<String> getErrors() {
    return errors;
  }
}
