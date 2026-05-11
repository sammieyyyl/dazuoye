package cn.jee.service;

import cn.jee.entity.User;
import cn.jee.repository.UserRepository;
import cn.jee.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Optional<String> getLoginUsername(HttpSession session) {
    Object username = session.getAttribute(SessionKeys.LOGIN_USER);
    if (username == null) {
      return Optional.empty();
    }
    String value = username.toString().trim();
    if (value.isBlank()) {
      return Optional.empty();
    }
    return Optional.of(value);
  }

  public User loginOrRegister(String name, HttpSession session) {
    String normalized = normalizeName(name);
    User user = userRepository.findByName(normalized).orElseGet(() -> {
      User created = new User();
      created.setName(normalized);
      return userRepository.save(created);
    });
    session.setAttribute(SessionKeys.LOGIN_USER, user.getName());
    return user;
  }

  public void logout(HttpSession session) {
    session.removeAttribute(SessionKeys.LOGIN_USER);
  }

  public List<User> listUsers() {
    return userRepository.findAll();
  }

  private String normalizeName(String name) {
    if (name == null) {
      return "";
    }
    return name.trim();
  }
}
