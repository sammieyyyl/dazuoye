package cn.jee;

import cn.jee.controller.UserController;
import cn.jee.service.UserService;
import cn.jee.web.Redirects;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class UserControllerTest {

  @Test
  void login_callsServiceAndRedirects() {
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    UserController controller = new UserController(userService);
    String view = controller.login("Tom", session);

    assertEquals(Redirects.MOVIE_LIST, view);
    verify(userService).loginOrRegister("Tom", session);
  }

  @Test
  void login_allowsNullName() {
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    UserController controller = new UserController(userService);
    String view = controller.login(null, session);

    assertEquals(Redirects.MOVIE_LIST, view);
    verify(userService).loginOrRegister(null, session);
  }
}
