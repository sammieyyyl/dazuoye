package cn.jee;

import cn.jee.controller.api.MovieApiController;
import cn.jee.service.MovieService;
import cn.jee.service.UploadService;
import cn.jee.service.UserService;
import cn.jee.web.dto.MovieDto;
import cn.jee.web.form.MovieCreateForm;
import cn.jee.web.form.MovieUpdateForm;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieApiControllerTest {

  @Test
  void list_requiresLogin() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());

    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.list(session);
    assertEquals(false, resp.get("success"));
  }

  @Test
  void list_returnsDtos() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(movieService.listDtosByUsername("Tom"))
      .thenReturn(List.of(new MovieDto("2025010101", "M", 1.0, "comment comment comment", "Tom", List.of())));

    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.list(session);
    assertEquals(true, resp.get("success"));
    assertNotNull(resp.get("data"));
  }

  @Test
  void create_rejectsInvalidForm() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    MovieCreateForm form = new MovieCreateForm();
    BindingResult br = new BeanPropertyBindingResult(form, "form");
    br.reject("x", "bad");

    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.create(form, br, session);
    assertEquals(false, resp.get("success"));
  }

  @Test
  void detail_requiresLogin_andHandlesMissing() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());
    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.detail("t", session);
    assertEquals(false, resp.get("success"));

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(movieService.findDtoByWatchTime("t")).thenReturn(Optional.empty());
    Map<String, Object> resp2 = controller.detail("t", session);
    assertEquals(false, resp2.get("success"));
  }

  @Test
  void detail_returnsDtoWhenFound() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(movieService.findDtoByWatchTime("t"))
      .thenReturn(Optional.of(new MovieDto("t", "M", 1.0, "comment comment comment", "Tom", List.of())));

    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.detail("t", session);
    assertEquals(true, resp.get("success"));
    assertNotNull(resp.get("data"));
  }

  @Test
  void create_success() {
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

    when(movieService.createMovieDto(form, "Tom"))
      .thenReturn(Optional.of(new MovieDto("2025010101", "M", 1.0, "comment comment comment", "Tom", List.of())));

    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.create(form, br, session);
    assertEquals(true, resp.get("success"));
  }

  @Test
  void update_returnsFailWhenNotLoggedInOrServiceEmpty() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    MovieUpdateForm form = new MovieUpdateForm();
    form.setName("N");
    form.setPrice(1.0);
    form.setComment("comment comment comment");
    BindingResult br = new BeanPropertyBindingResult(form, "form");

    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());
    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.update("t", form, br, session);
    assertEquals(false, resp.get("success"));

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(movieService.updateMovieDto("t", form, "Tom")).thenReturn(Optional.empty());
    Map<String, Object> resp2 = controller.update("t", form, br, session);
    assertEquals(false, resp2.get("success"));
  }

  @Test
  void delete_success() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(movieService.deleteMovie("t", "Tom")).thenReturn(true);

    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.delete("t", session);
    assertEquals(true, resp.get("success"));
  }

  @Test
  void upload_requiresLogin_andReturnsOkWhenSuccess() throws IOException {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);
    MultipartFile file = mock(MultipartFile.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());
    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.upload("t", file, session);
    assertEquals(false, resp.get("success"));

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(uploadService.saveToUploads(file)).thenReturn("x.png");
    when(movieService.addImageDto("t", "x.png", "Tom"))
      .thenReturn(Optional.of(new MovieDto("t", "M", 1.0, "comment comment comment", "Tom", List.of("x.png"))));

    Map<String, Object> resp2 = controller.upload("t", file, session);
    assertEquals(true, resp2.get("success"));
    verify(uploadService, never()).deleteFromUploads("x.png");
  }

  @Test
  void upload_rollsBackWhenMovieUpdateFails() throws IOException {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    HttpSession session = mock(HttpSession.class);
    MultipartFile file = mock(MultipartFile.class);

    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));
    when(uploadService.saveToUploads(file)).thenReturn("x.png");
    when(movieService.addImageDto("t", "x.png", "Tom")).thenReturn(Optional.empty());

    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.upload("t", file, session);
    assertEquals(false, resp.get("success"));
    verify(uploadService).deleteFromUploads("x.png");
  }

  @Test
  void update_success() {
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

    when(movieService.updateMovieDto("t", form, "Tom"))
      .thenReturn(Optional.of(new MovieDto("t", "N", 1.0, "comment comment comment", "Tom", List.of())));

    MovieApiController controller = new MovieApiController(movieService, uploadService, userService);
    Map<String, Object> resp = controller.update("t", form, br, session);
    assertEquals(true, resp.get("success"));
  }
}
