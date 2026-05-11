package cn.jee.config;

import cn.jee.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
  private final LoginInterceptor loginInterceptor;

  public WebMvcConfig(LoginInterceptor loginInterceptor) {
    this.loginInterceptor = loginInterceptor;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(loginInterceptor)
      .addPathPatterns("/**");
  }

  @Override
  public void addResourceHandlers(ResourceHandlerRegistry registry) {
    Path uploads = Paths.get("uploads").toAbsolutePath().normalize();
    String location = uploads.toUri().toString();
    registry.addResourceHandler("/uploads/**")
      .addResourceLocations(location);
  }
}
