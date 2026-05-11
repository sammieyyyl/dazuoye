package cn.jee.controller;

import cn.jee.entity.User;
import cn.jee.repository.UserRepository;
import cn.jee.web.Redirects;
import cn.jee.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
public class UserController {
  private final UserRepository userRepository;

  public UserController(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @PostMapping("/login")
  public String login(String name, HttpSession session) {
    if (name == null) {
      name = "";
    }
    var userOpt = userRepository.findByName(name);
    if (userOpt.isEmpty()) {
      User user = new User();
      user.setName(name);
      userRepository.save(user);
    }
    session.setAttribute(SessionKeys.LOGIN_USER, name);
    return Redirects.MOVIE_LIST;
  }
}
