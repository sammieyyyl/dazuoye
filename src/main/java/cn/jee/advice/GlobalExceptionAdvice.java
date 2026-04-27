package cn.jee.advice;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionAdvice {
  @ExceptionHandler(IOException.class)
  public Map<String, Object> handleIOException(IOException ex) {
    return Map.of(
      "success", false,
      "message", ex.getMessage()
    );
  }
}