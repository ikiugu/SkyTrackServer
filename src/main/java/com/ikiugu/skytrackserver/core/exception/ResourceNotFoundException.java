package com.ikiugu.skytrackserver.core.exception;

import org.springframework.http.HttpStatus;

public class ResourceNotFoundException extends ApiException {
  public ResourceNotFoundException(String message) {
    super(message, HttpStatus.NOT_FOUND);
  }

  public ResourceNotFoundException(String resourceType, String identifier) {
    super(
        String.format("%s with identifier '%s' not found", resourceType, identifier),
        HttpStatus.NOT_FOUND);
  }
}
