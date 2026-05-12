package cn.jee;

import cn.jee.entity.User;
import cn.jee.repository.UserRepository;
import cn.jee.service.UserService;
import cn.jee.web.SessionKeys;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

  @Test
  void loginOrRegister_createsUserWhenMissing_andWritesSession() {
    UserRepository userRepository = mock(UserRepository.class);
    HttpSession session = mock(HttpSession.class);

    when(userRepository.findByName("Tom")).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

    UserService userService = new UserService(userRepository);
    User user = userService.loginOrRegister("Tom", session);

    assertEquals("Tom", user.getName());

    ArgumentCaptor<User> saved = ArgumentCaptor.forClass(User.class);
    verify(userRepository).save(saved.capture());
    assertEquals("Tom", saved.getValue().getName());

    verify(session).setAttribute(SessionKeys.LOGIN_USER, "Tom");
  }

  @Test
  void getLoginUsername_returnsEmptyWhenMissingOrBlank() {
    UserRepository userRepository = mock(UserRepository.class);
    UserService userService = new UserService(userRepository);

    HttpSession session = mock(HttpSession.class);
    when(session.getAttribute(SessionKeys.LOGIN_USER)).thenReturn(null);
    assertTrue(userService.getLoginUsername(session).isEmpty());

    when(session.getAttribute(SessionKeys.LOGIN_USER)).thenReturn("   ");
    assertTrue(userService.getLoginUsername(session).isEmpty());
  }
}
