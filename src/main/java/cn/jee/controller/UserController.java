package cn.jee.controller;

import cn.jee.service.UserService;
import cn.jee.web.Redirects;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @PostMapping("/login")
  public String login(String name, HttpSession session) {
    userService.loginOrRegister(name, session);
    return Redirects.MOVIE_LIST;
  }
}
