package cn.jee.interceptor;

import cn.jee.web.Redirects;
import cn.jee.web.SessionKeys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;

@Component
public class LoginInterceptor implements HandlerInterceptor {
  private static final Set<String> ALLOWED_PREFIXES = Set.of(
    "/",
    "/index",
    "/user/login",
    "/error",
    "/uploads/"
  );

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    String uri = request.getRequestURI();
    if (uri == null) {
      response.sendRedirect(Redirects.ROOT);
      return false;
    }
    if (isAllowed(uri)) {
      return true;
    }
    HttpSession session = request.getSession(false);
    if (session == null) {
      response.sendRedirect(Redirects.ROOT);
      return false;
    }
    Object user = session.getAttribute(SessionKeys.LOGIN_USER);
    if (user == null || user.toString().trim().isBlank()) {
      response.sendRedirect(Redirects.ROOT);
      return false;
    }
    return true;
  }

  private boolean isAllowed(String uri) {
    for (String prefix : ALLOWED_PREFIXES) {
      if (prefix.endsWith("/") && uri.startsWith(prefix)) {
        return true;
      }
      if (!prefix.endsWith("/") && uri.equals(prefix)) {
        return true;
      }
    }
    return false;
  }
}
