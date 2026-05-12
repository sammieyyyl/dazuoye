package cn.jee;

import cn.jee.controller.MovieController;
import cn.jee.entity.Movie;
import cn.jee.service.MovieService;
import cn.jee.service.UploadService;
import cn.jee.service.UserService;
import cn.jee.web.Redirects;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieControllerUploadTest {

  @Test
  void images_returnsEmptyWhenNotLoggedIn() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.empty());

    List<String> images = controller.images("t", session);
    assertTrue(images.isEmpty());
  }

  @Test
  void upload_savesImageNameToMovie() throws IOException {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));

    MockMultipartFile file = new MockMultipartFile(
      "file",
      "a.txt",
      "text/plain",
      "hello".getBytes()
    );

    when(uploadService.saveToUploads(file)).thenReturn("a.txt");
    when(movieService.addImage("t", "a.txt", "Tom")).thenReturn(Optional.of(new Movie()));

    String view = controller.upload("t", file, session);
    assertEquals(Redirects.MOVIE_LIST, view);
    verify(uploadService).saveToUploads(file);
    verify(movieService).addImage("t", "a.txt", "Tom");
  }

  @Test
  void upload_deletesStoredFileWhenMovieUpdateFails() throws IOException {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));

    MockMultipartFile file = new MockMultipartFile(
      "file",
      "a.txt",
      "text/plain",
      "hello".getBytes()
    );

    when(uploadService.saveToUploads(file)).thenReturn("a.txt");
    when(movieService.addImage("t", "a.txt", "Tom")).thenReturn(Optional.empty());

    String view = controller.upload("t", file, session);
    assertEquals(Redirects.MOVIE_LIST, view);
    verify(uploadService).deleteFromUploads("a.txt");
  }

  @Test
  void upload_throwsWhenFileMissing() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));

    assertThrows(IOException.class, () -> controller.upload("t", null, session));
  }
}
