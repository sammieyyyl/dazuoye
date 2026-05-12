package cn.jee;

import cn.jee.controller.api.UserApiController;
import cn.jee.entity.User;
import cn.jee.service.UserService;
import cn.jee.web.form.LoginForm;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserApiControllerTest {

  @Test
  void me_requiresLogin() {
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());

    UserApiController controller = new UserApiController(userService);
    Map<String, Object> resp = controller.me(session);
    assertEquals(false, resp.get("success"));
  }

  @Test
  void login_validatesBindingResult() {
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    LoginForm form = new LoginForm();
    BindingResult br = new BeanPropertyBindingResult(form, "form");
    br.reject("x", "bad");

    UserApiController controller = new UserApiController(userService);
    Map<String, Object> resp = controller.login(form, br, session);
    assertEquals(false, resp.get("success"));
  }

  @Test
  void login_callsServiceAndReturnsUsername() {
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    LoginForm form = new LoginForm();
    form.setName("Tom");
    BindingResult br = new BeanPropertyBindingResult(form, "form");

    User user = new User();
    user.setName("Tom");
    when(userService.loginOrRegister("Tom", session)).thenReturn(user);

    UserApiController controller = new UserApiController(userService);
    Map<String, Object> resp = controller.login(form, br, session);
    assertEquals(true, resp.get("success"));
    assertNotNull(resp.get("data"));
  }

  @Test
  void logout_callsService() {
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    UserApiController controller = new UserApiController(userService);
    Map<String, Object> resp = controller.logout(session);
    assertEquals(true, resp.get("success"));
    verify(userService).logout(session);
  }
}
