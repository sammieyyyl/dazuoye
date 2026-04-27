package cn.jee.controller;

import cn.jee.entity.User;
import cn.jee.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/user")
public class UserController {
  @Autowired
  UserRepository userRepository;

  @RequestMapping(value = "/login", method = RequestMethod.POST)
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
    session.setAttribute("login_user", name);
    return "redirect:/movie/list";
  }
}