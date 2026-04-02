package com.example.couple.exception;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@AllArgsConstructor
public class GlobalExceptionHandler {
  @ExceptionHandler(BadRequestException.class)
  public ProblemDetail handleRuntimeException(RuntimeException ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  @ExceptionHandler(org.springframework.web.bind.MethodArgumentNotValidException.class)
  public ProblemDetail handleValidationException(
      org.springframework.web.bind.MethodArgumentNotValidException ex) {
    String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
    return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, errorMessage);
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGlobalException(Exception ex) {
    return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Sistem hatası");
  }
}
