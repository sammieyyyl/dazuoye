package cn.jee.controller;

import cn.jee.service.MovieService;
import cn.jee.service.UserService;
import cn.jee.web.Redirects;
import cn.jee.web.Views;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserPageController {
  private final UserService userService;
  private final MovieService movieService;

  public UserPageController(UserService userService, MovieService movieService) {
    this.userService = userService;
    this.movieService = movieService;
  }

  @RequestMapping("/profile")
  public String profile(Model model, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    String username = usernameOpt.get();
    model.addAttribute("username", username);
    model.addAttribute("movieCount", movieService.listByUsername(username).size());
    return Views.USER_PROFILE;
  }

  @PostMapping("/logout")
  public String logout(HttpSession session) {
    userService.logout(session);
    return Redirects.ROOT;
  }

  @RequestMapping("/list")
  public String listUsers(Model model, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    model.addAttribute("username", usernameOpt.get());
    model.addAttribute("users", userService.listUsers());
    return Views.USER_LIST;
  }
}
