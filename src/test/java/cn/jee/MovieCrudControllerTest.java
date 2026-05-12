package cn.jee;

import cn.jee.controller.MovieCrudController;
import cn.jee.entity.Movie;
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

class MovieCrudControllerTest {

  @Test
  void detail_redirectsWhenNotLoggedIn() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    String view = controller.detail("t", new ExtendedModelMap(), session);
    assertEquals(Redirects.ROOT, view);
  }

  @Test
  void detail_putsMovieIntoModel() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));

    MovieDto dto = new MovieDto("t", "M", 1.0, "comment comment comment", "Tom", List.of("a.png"));
    when(movieService.findDtoByWatchTime("t")).thenReturn(Optional.of(dto));

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    ExtendedModelMap model = new ExtendedModelMap();
    String view = controller.detail("t", model, session);
    assertEquals(Views.MOVIE_DETAIL, view);
    assertNotNull(model.get("movie"));
    assertEquals(List.of("/uploads/a.png"), model.get("images"));
  }

  @Test
  void update_callsServiceWhenValid() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));

    MovieUpdateForm form = new MovieUpdateForm();
    form.setName("N");
    form.setPrice(1.0);
    form.setComment("comment comment comment");
    BindingResult br = new BeanPropertyBindingResult(form, "form");

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    String view = controller.update("t", form, br, session);
    assertEquals(Redirects.MOVIE_LIST, view);
    verify(movieService).updateMovie(eq("t"), any(Movie.class), eq("Tom"));
  }

  @Test
  void removeImage_deletesFileAndUpdatesMovie() throws IOException {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    String view = controller.removeImage("t", "a.png", session);
    assertEquals(Redirects.MOVIE_LIST, view);
    verify(movieService).removeImage("t", "a.png", "Tom");
    verify(uploadService).deleteFromUploads("a.png");
  }

  @Test
  void uploadToMovie_savesFileAndAddsImage() throws IOException {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);
    MultipartFile file = mock(MultipartFile.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(uploadService.saveToUploads(file)).thenReturn("x.png");

    MovieCrudController controller = new MovieCrudController(movieService, uploadService, userService);
    String view = controller.uploadToMovie("t", file, session);
    assertEquals(Redirects.MOVIE_LIST, view);
    verify(movieService).addImage("t", "x.png", "Tom");
  }
}
