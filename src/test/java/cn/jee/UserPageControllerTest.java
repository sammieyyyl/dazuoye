package cn.jee;

import cn.jee.controller.UserPageController;
import cn.jee.entity.User;
import cn.jee.service.MovieService;
import cn.jee.service.UserService;
import cn.jee.web.Redirects;
import cn.jee.web.Views;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserPageControllerTest {

  @Test
  void profile_requiresLogin() {
    UserService userService = mock(UserService.class);
    MovieService movieService = mock(MovieService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());

    UserPageController controller = new UserPageController(userService, movieService);
    String view = controller.profile(new ExtendedModelMap(), session);
    assertEquals(Redirects.ROOT, view);
  }

  @Test
  void profile_setsMovieCount() {
    UserService userService = mock(UserService.class);
    MovieService movieService = mock(MovieService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(movieService.listByUsername("Tom")).thenReturn(List.of());

    UserPageController controller = new UserPageController(userService, movieService);
    ExtendedModelMap model = new ExtendedModelMap();
    String view = controller.profile(model, session);
    assertEquals(Views.USER_PROFILE, view);
    assertEquals(0, model.get("movieCount"));
  }

  @Test
  void listUsers_setsUsers() {
    UserService userService = mock(UserService.class);
    MovieService movieService = mock(MovieService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(userService.listUsers()).thenReturn(List.of(new User()));

    UserPageController controller = new UserPageController(userService, movieService);
    ExtendedModelMap model = new ExtendedModelMap();
    String view = controller.listUsers(model, session);
    assertEquals(Views.USER_LIST, view);
    assertNotNull(model.get("users"));
  }

  @Test
  void logout_callsService() {
    UserService userService = mock(UserService.class);
    MovieService movieService = mock(MovieService.class);
    HttpSession session = mock(HttpSession.class);

    UserPageController controller = new UserPageController(userService, movieService);
    String view = controller.logout(session);
    assertEquals(Redirects.ROOT, view);
    verify(userService).logout(session);
  }
}
