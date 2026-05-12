package cn.jee;

import cn.jee.controller.MovieController;
import cn.jee.service.MovieService;
import cn.jee.service.UploadService;
import cn.jee.service.UserService;
import cn.jee.web.Redirects;
import cn.jee.web.Views;
import cn.jee.web.dto.MovieDto;
import cn.jee.web.form.MovieCreateForm;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieControllerTest {

  @Test
  void list_redirectsWhenNotLoggedIn() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());

    String view = controller.list(new ExtendedModelMap(), session);
    assertEquals(Redirects.ROOT, view);
  }

  @Test
  void newPage_putsFormIntoModel() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));

    ExtendedModelMap model = new ExtendedModelMap();
    String view = controller.newPage(model, session);
    assertEquals(Views.MOVIE_NEW, view);
    assertNotNull(model.get("form"));
  }

  @Test
  void save_persistsMovieWhenValid() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));

    MovieCreateForm form = new MovieCreateForm();
    form.setWatchTime("2025010101");
    form.setName("M");
    form.setPrice(1.0);
    form.setComment("comment comment comment");
    BindingResult br = new BeanPropertyBindingResult(form, "form");

    when(movieService.createMovieDto(form, "Tom"))
      .thenReturn(Optional.of(new MovieDto("2025010101", "M", 1.0, "comment comment comment", "Tom", List.of())));

    String view = controller.save(form, br, session);
    assertEquals(Redirects.MOVIE_LIST, view);
    verify(movieService).createMovieDto(form, "Tom");
  }

  @Test
  void list_returnsViewAndModelWhenLoggedIn() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(movieService.listDtosByUsername("Tom")).thenReturn(List.of());

    ExtendedModelMap model = new ExtendedModelMap();
    String view = controller.list(model, session);
    assertEquals(Views.MOVIE_LIST, view);
    assertEquals("Tom", model.get("username"));
  }

  @Test
  void save_returnsNewWhenBindingHasErrors() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));

    MovieCreateForm form = new MovieCreateForm();
    BindingResult br = new BeanPropertyBindingResult(form, "form");
    br.reject("x", "bad");

    String view = controller.save(form, br, session);
    assertEquals(Views.MOVIE_NEW, view);
    verifyNoInteractions(movieService);
  }

  @Test
  void save_redirectsRootWhenServiceReturnsEmpty() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));

    MovieCreateForm form = new MovieCreateForm();
    form.setWatchTime("2025010101");
    form.setName("M");
    form.setPrice(1.0);
    form.setComment("comment comment comment");
    BindingResult br = new BeanPropertyBindingResult(form, "form");

    when(movieService.createMovieDto(form, "Tom")).thenReturn(Optional.empty());
    String view = controller.save(form, br, session);
    assertEquals(Redirects.ROOT, view);
  }

  @Test
  void uploadPage_redirectsRootWhenNotLoggedIn() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());
    String view = controller.uploadPage("t", new ExtendedModelMap(), session);
    assertEquals(Redirects.ROOT, view);
  }

  @Test
  void upload_redirectsRootWhenNotLoggedIn() throws IOException {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());
    MultipartFile file = mock(MultipartFile.class);

    String view = controller.upload("t", file, session);
    assertEquals(Redirects.ROOT, view);
    verifyNoInteractions(uploadService);
  }

  @Test
  void images_returnsServiceListWhenLoggedIn() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(movieService.listImages("t", "Tom")).thenReturn(List.of("a.png"));

    List<String> images = controller.images("t", session);
    assertEquals(List.of("a.png"), images);
  }
}
