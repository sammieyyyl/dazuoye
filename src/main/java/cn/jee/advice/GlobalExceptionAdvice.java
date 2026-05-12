package cn.jee.advice;

import cn.jee.web.dto.ApiResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionAdvice {
  @ExceptionHandler(IOException.class)
  public Map<String, Object> handleIOException(IOException ex) {
    return ApiResponse.fail(ex.getMessage());
  }
}
