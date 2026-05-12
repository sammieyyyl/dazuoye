package cn.jee;

import cn.jee.controller.MovieCrudController;
import cn.jee.service.MovieService;
import cn.jee.service.UploadService;
import cn.jee.service.UserService;
import cn.jee.web.Redirects;
import cn.jee.web.Views;
import cn.jee.web.dto.MovieDto;
import cn.jee.web.form.MovieUpdateForm;
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

class MovieCrudControllerMoreTest {

  @Test
  void edit_redirectsWhenMovieMissing() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(movieService.findDtoByWatchTime("t")).thenReturn(Optional.empty());

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    String view = controller.edit("t", new ExtendedModelMap(), session);
    assertEquals(Redirects.MOVIE_LIST, view);
  }

  @Test
  void edit_returnsForm() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    MovieDto dto = new MovieDto("t", "M", 1.0, "comment comment comment", "Tom", List.of("a.png"));
    when(movieService.findDtoByWatchTime("t")).thenReturn(Optional.of(dto));

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    ExtendedModelMap model = new ExtendedModelMap();
    String view = controller.edit("t", model, session);
    assertEquals(Views.MOVIE_EDIT, view);
    assertNotNull(model.get("form"));
  }

  @Test
  void update_returnsListWhenBindingHasErrors() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    MovieUpdateForm form = new MovieUpdateForm();
    BindingResult br = new BeanPropertyBindingResult(form, "form");
    br.reject("x", "bad");

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    String view = controller.update("t", form, br, session);
    assertEquals(Redirects.MOVIE_LIST, view);
    verify(movieService, never()).updateMovie(any(), any(), any());
  }

  @Test
  void detail_redirectsWhenMovieMissing() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(movieService.findDtoByWatchTime("t")).thenReturn(Optional.empty());

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    String view = controller.detail("t", new ExtendedModelMap(), session);
    assertEquals(Redirects.MOVIE_LIST, view);
  }

  @Test
  void detail_setsEmptyImagesWhenDtoImagesNull() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    MovieDto dto = new MovieDto("t", "M", 1.0, "comment comment comment", "Tom", null);
    when(movieService.findDtoByWatchTime("t")).thenReturn(Optional.of(dto));

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    ExtendedModelMap model = new ExtendedModelMap();
    String view = controller.detail("t", model, session);
    assertEquals(Views.MOVIE_DETAIL, view);
    assertEquals(List.of(), model.get("images"));
  }

  @Test
  void edit_redirectsRootWhenNotLoggedIn() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    String view = controller.edit("t", new ExtendedModelMap(), session);
    assertEquals(Redirects.ROOT, view);
  }

  @Test
  void update_redirectsRootWhenNotLoggedIn() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());

    MovieUpdateForm form = new MovieUpdateForm();
    BindingResult br = new BeanPropertyBindingResult(form, "form");
    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    String view = controller.update("t", form, br, session);
    assertEquals(Redirects.ROOT, view);
  }

  @Test
  void delete_redirectsRootWhenNotLoggedIn() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    String view = controller.delete("t", session);
    assertEquals(Redirects.ROOT, view);
  }

  @Test
  void removeImage_redirectsRootWhenNotLoggedIn() throws IOException {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    String view = controller.removeImage("t", "a.png", session);
    assertEquals(Redirects.ROOT, view);
  }

  @Test
  void uploadToMovie_redirectsRootWhenNotLoggedIn() throws IOException {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);
    MultipartFile file = mock(MultipartFile.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    String view = controller.uploadToMovie("t", file, session);
    assertEquals(Redirects.ROOT, view);
  }
}
