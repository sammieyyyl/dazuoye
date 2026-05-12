package cn.jee;

import cn.jee.advice.GlobalExceptionAdvice;
import cn.jee.config.WebMvcConfig;
import cn.jee.controller.IndexController;
import cn.jee.interceptor.LoginInterceptor;
import cn.jee.util.FileNameUtils;
import cn.jee.web.Views;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

import jakarta.servlet.ServletContext;

import java.io.IOException;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class Jee2026ExamAApplicationTests {

  @Test
  void contextLoads() {
    assertEquals("file", FileNameUtils.sanitizeBaseName(null));
    assertEquals("abc.txt", FileNameUtils.sanitizeBaseName("abc.txt"));
    assertEquals("a_b_c.txt", FileNameUtils.sanitizeBaseName("a b c.txt"));
    String unique = FileNameUtils.uniqueFileName("a b.txt");
    assertTrue(unique.endsWith(".txt"));
    assertTrue(unique.contains("_"));
  }

  @Test
  void globalExceptionAdvice_mapsIOExceptionToResponse() {
    GlobalExceptionAdvice advice = new GlobalExceptionAdvice();
    Map<String, Object> resp = advice.handleIOException(new IOException("x"));
    assertEquals(false, resp.get("success"));
    assertEquals("x", resp.get("message"));
  }

  @Test
  void webMvcConfig_registersInterceptorAndUploadsMapping() {
    LoginInterceptor interceptor = mock(LoginInterceptor.class);
    WebMvcConfig config = new WebMvcConfig(interceptor);

    InterceptorRegistry interceptorRegistry = new InterceptorRegistry();
    config.addInterceptors(interceptorRegistry);
    try {
      var f = InterceptorRegistry.class.getDeclaredField("registrations");
      f.setAccessible(true);
      var regs = (java.util.List<?>) f.get(interceptorRegistry);
      assertFalse(regs.isEmpty());
    } catch (ReflectiveOperationException e) {
      fail(e);
    }

    ApplicationContext applicationContext = mock(ApplicationContext.class);
    ServletContext servletContext = mock(ServletContext.class);
    ResourceHandlerRegistry resourceHandlerRegistry = new ResourceHandlerRegistry(applicationContext, servletContext);
    config.addResourceHandlers(resourceHandlerRegistry);
    assertTrue(resourceHandlerRegistry.hasMappingForPattern("/uploads/**"));
  }

  @Test
  void indexController_returnsIndexView() {
    IndexController controller = new IndexController();
    assertEquals(Views.INDEX, controller.index());
  }

  @Test
  void fileNameUtils_extensionLowerCase_handlesEdgeCases() {
    assertEquals("", FileNameUtils.extensionLowerCase(null));
    assertEquals("", FileNameUtils.extensionLowerCase("abc"));
    assertEquals("", FileNameUtils.extensionLowerCase("a."));
    assertEquals("txt", FileNameUtils.extensionLowerCase("a.TXT"));
  }

  @Test
  void fileNameUtils_sanitizeBaseName_stripsPathAndTruncates() {
    assertEquals("a.txt", FileNameUtils.sanitizeBaseName("C:\\x\\a.txt"));
    assertEquals("file", FileNameUtils.sanitizeBaseName("   "));
    String longName = "a".repeat(200) + ".txt";
    assertTrue(FileNameUtils.sanitizeBaseName(longName).length() <= 80);
  }

  @Test
  void fileNameUtils_uniqueFileName_handlesNoExtAndBlankBase() {
    String noExt = FileNameUtils.uniqueFileName("abc");
    assertTrue(noExt.contains("_"));

    String blank = FileNameUtils.uniqueFileName("   ");
    assertTrue(blank.contains("_"));

    String onlyExt = FileNameUtils.uniqueFileName(".txt");
    assertTrue(onlyExt.endsWith(".txt"));
  }

}
