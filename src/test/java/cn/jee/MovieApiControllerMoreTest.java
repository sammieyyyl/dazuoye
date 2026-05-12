package cn.jee;

import cn.jee.controller.api.MovieApiController;
import cn.jee.service.MovieService;
import cn.jee.service.UploadService;
import cn.jee.service.UserService;
import cn.jee.web.form.MovieCreateForm;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class MovieApiControllerMoreTest {

  @Test
  void delete_returnsFailWhenServiceReturnsFalse() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(movieService.deleteMovie("t", "Tom")).thenReturn(false);

    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.delete("t", session);
    assertEquals(false, resp.get("success"));
  }

  @Test
  void create_returnsFailWhenCreateMovieEmpty() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    MovieCreateForm form = new MovieCreateForm();
    form.setWatchTime("2025010101");
    form.setName("M");
    form.setPrice(1.0);
    form.setComment("comment comment comment");
    BindingResult br = new BeanPropertyBindingResult(form, "form");
    when(movieService.createMovieDto(form, "Tom")).thenReturn(Optional.empty());

    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.create(form, br, session);
    assertEquals(false, resp.get("success"));
  }
}
