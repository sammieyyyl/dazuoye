package cn.jee;

import cn.jee.entity.User;
import cn.jee.repository.UserRepository;
import cn.jee.service.UserService;
import cn.jee.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceMoreTest {

  @Test
  void listUsers_delegatesToRepository() {
    UserRepository userRepository = mock(UserRepository.class);
    when(userRepository.findAll()).thenReturn(List.of(new User()));

    UserService userService = new UserService(userRepository);
    assertEquals(1, userService.listUsers().size());
  }

  @Test
  void logout_removesSessionAttribute() {
    UserRepository userRepository = mock(UserRepository.class);
    UserService userService = new UserService(userRepository);
    HttpSession session = mock(HttpSession.class);

    userService.logout(session);
    verify(session).removeAttribute(SessionKeys.LOGIN_USER);
  }

  @Test
  void getLoginUsername_trimsWhitespace() {
    UserRepository userRepository = mock(UserRepository.class);
    UserService userService = new UserService(userRepository);
    HttpSession session = mock(HttpSession.class);

    when(session.getAttribute(SessionKeys.LOGIN_USER)).thenReturn(" Tom ");
    Optional<String> username = userService.getLoginUsername(session);
    assertTrue(username.isPresent());
    assertEquals("Tom", username.get());
  }
}
