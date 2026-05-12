package cn.jee;

import cn.jee.controller.MovieController;
import cn.jee.service.MovieService;
import cn.jee.service.UploadService;
import cn.jee.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieControllerImagesEdgeTest {

  @Test
  void images_returnsEmptyWhenMovieMissingOrNotOwner() {
    MovieService movieService = mock(MovieService.class);
    UploadService uploadService = mock(UploadService.class);
    UserService userService = mock(UserService.class);
    MovieController controller = new MovieController(movieService, uploadService, userService);

    HttpSession session = mock(HttpSession.class);
    when(userService.getLoginUsername(session)).thenReturn(Optional.of("Tom"));

    when(movieService.listImages("t", "Tom")).thenReturn(List.of());
    assertTrue(controller.images("t", session).isEmpty());

    when(movieService.listImages("t2", "Tom")).thenReturn(List.of());
    List<String> images = controller.images("t2", session);
    assertTrue(images.isEmpty());
  }
}
