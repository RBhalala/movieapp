package com.backbase.movieapp.middleware;

import com.backbase.movieapp.dto.ErrorResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.stream.Collectors;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
@NoArgsConstructor
public class ApiExceptionHandler {
  @ExceptionHandler(OmdbApiException.class)
  public ResponseEntity<ErrorResponse> handle(OmdbApiException ex) {
    log.error("Error : {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse(ex.getMessage()));
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
    String errorMessage = ex.getConstraintViolations().stream()
            .map(v->{
              String fullPath = v.getPropertyPath().toString();
              String field = fullPath.contains(".")
                      ? fullPath.substring(fullPath.lastIndexOf('.') + 1)
                      : fullPath;
              return field + ": " + v.getMessage();
            })
            .collect(Collectors.joining(", "));
    return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(new ErrorResponse(errorMessage));
  }
}
