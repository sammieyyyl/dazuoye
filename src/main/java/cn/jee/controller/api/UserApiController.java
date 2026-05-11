package cn.jee.controller.api;

import cn.jee.service.UserService;
import cn.jee.web.dto.ApiResponse;
import cn.jee.web.form.LoginForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserApiController {
  private final UserService userService;

  public UserApiController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me")
  public Map<String, Object> me(HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return ApiResponse.fail("未登录");
    }
    return ApiResponse.ok("ok", Map.of("username", usernameOpt.get()));
  }

  @PostMapping("/login")
  public Map<String, Object> login(@Valid LoginForm form, BindingResult bindingResult, HttpSession session) {
    if (bindingResult.hasErrors()) {
      return ApiResponse.fail("参数不合法", bindingResult.getAllErrors());
    }
    var user = userService.loginOrRegister(form.getName(), session);
    return ApiResponse.ok("ok", Map.of("username", user.getName()));
  }

  @PostMapping("/logout")
  public Map<String, Object> logout(HttpSession session) {
    userService.logout(session);
    return ApiResponse.ok("ok");
  }
}
