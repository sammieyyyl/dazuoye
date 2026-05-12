package cn.jee;

import cn.jee.web.dto.ApiResponse;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

  @Test
  void okAndFail_haveExpectedKeys() {
    Map<String, Object> ok = ApiResponse.ok();
    assertEquals(true, ok.get("success"));

    Map<String, Object> okMsg = ApiResponse.ok("hello");
    assertEquals(true, okMsg.get("success"));
    assertEquals("hello", okMsg.get("message"));

    Map<String, Object> fail = ApiResponse.fail("bad");
    assertEquals(false, fail.get("success"));
    assertEquals("bad", fail.get("message"));
  }
}
