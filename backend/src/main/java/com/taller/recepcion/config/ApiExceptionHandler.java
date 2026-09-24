package com.taller.recepcion.config;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {
  @ExceptionHandler(BadCredentialsException.class)
  ResponseEntity<Map<String, String>> badCredentials(BadCredentialsException exception) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", exception.getMessage()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  ResponseEntity<Map<String, String>> illegalArgument(IllegalArgumentException exception) {
    return ResponseEntity.badRequest().body(Map.of("message", exception.getMessage()));
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<Map<String, String>> validation(MethodArgumentNotValidException exception) {
    String message = exception.getBindingResult().getFieldErrors().stream()
        .findFirst()
        .map(error -> error.getField() + ": " + error.getDefaultMessage())
        .orElse("Solicitud invalida");
    return ResponseEntity.badRequest().body(Map.of("message", message));
  }
}
