package cn.jee.web.dto;

import java.util.Map;

public final class ApiResponse {
  private static final String KEY_SUCCESS = "success";
  private static final String KEY_MESSAGE = "message";
  private static final String KEY_DATA = "data";

  private ApiResponse() {
  }

  public static Map<String, Object> ok() {
    return Map.of(KEY_SUCCESS, true);
  }

  public static Map<String, Object> ok(String message) {
    return Map.of(
      KEY_SUCCESS, true,
      KEY_MESSAGE, message
    );
  }

  public static Map<String, Object> ok(String message, Object data) {
    return Map.of(
      KEY_SUCCESS, true,
      KEY_MESSAGE, message,
      KEY_DATA, data
    );
  }

  public static Map<String, Object> fail(String message) {
    return Map.of(
      KEY_SUCCESS, false,
      KEY_MESSAGE, message
    );
  }

  public static Map<String, Object> fail(String message, Object data) {
    return Map.of(
      KEY_SUCCESS, false,
      KEY_MESSAGE, message,
      KEY_DATA, data
    );
  }
}
