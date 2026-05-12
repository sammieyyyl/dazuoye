package cn.jee;

import cn.jee.interceptor.LoginInterceptor;
import cn.jee.web.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginInterceptorTest {

  @Test
  void preHandle_allowsPublicRoutes() throws Exception {
    LoginInterceptor interceptor = new LoginInterceptor();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getRequestURI()).thenReturn("/");
    assertTrue(interceptor.preHandle(request, response, new Object()));

    when(request.getRequestURI()).thenReturn("/uploads/a.png");
    assertTrue(interceptor.preHandle(request, response, new Object()));
  }

  @Test
  void preHandle_redirectsWhenNoSession() throws Exception {
    LoginInterceptor interceptor = new LoginInterceptor();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);

    when(request.getRequestURI()).thenReturn("/movie/list");
    when(request.getSession(false)).thenReturn(null);

    assertFalse(interceptor.preHandle(request, response, new Object()));
    verify(response).sendRedirect("/");
  }

  @Test
  void preHandle_redirectsWhenNotLoggedIn() throws Exception {
    LoginInterceptor interceptor = new LoginInterceptor();
    HttpServletRequest request = mock(HttpServletRequest.class);
    HttpServletResponse response = mock(HttpServletResponse.class);
    HttpSession session = mock(HttpSession.class);

    when(request.getRequestURI()).thenReturn("/movie/list");
    when(request.getSession(false)).thenReturn(session);
    when(session.getAttribute(SessionKeys.LOGIN_USER)).thenReturn("   ");

    assertFalse(interceptor.preHandle(request, response, new Object()));
    verify(response).sendRedirect("/");
  }
}
