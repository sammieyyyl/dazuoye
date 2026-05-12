package cn.jee;

import cn.jee.entity.Movie;
import cn.jee.entity.User;
import cn.jee.repository.MovieRepository;
import cn.jee.repository.UserRepository;
import cn.jee.service.MovieService;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class MovieServiceEdgeCaseTest {

  @Test
  void updateMovie_returnsEmptyWhenNotOwner() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    User owner = new User();
    owner.setName("Owner");
    Movie movie = new Movie();
    movie.setUser(owner);
    when(movieRepository.findByWatchTime("t")).thenReturn(Optional.of(movie));

    Movie updates = new Movie();
    updates.setName("N");

    assertTrue(movieService.updateMovie("t", updates, "Other").isEmpty());
  }

  @Test
  void deleteMovie_returnsFalseWhenMissingOrNotOwner() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    assertFalse(movieService.deleteMovie("t", "Tom"));

    User owner = new User();
    owner.setName("Owner");
    Movie movie = new Movie();
    movie.setUser(owner);
    when(movieRepository.findByWatchTime("t")).thenReturn(Optional.of(movie));
    assertFalse(movieService.deleteMovie("t", "Other"));
  }

  @Test
  void createMovie_returnsEmptyOnInvalidArgsOrMissingUser() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    assertTrue(movieService.createMovie(null, "Tom").isEmpty());
    assertTrue(movieService.createMovie(new Movie(), null).isEmpty());
    assertTrue(movieService.createMovie(new Movie(), " ").isEmpty());

    when(userRepository.findByName("Tom")).thenReturn(Optional.empty());
    assertTrue(movieService.createMovie(new Movie(), "Tom").isEmpty());
  }

  @Test
  void updateMovie_returnsEmptyWhenUpdatesNullOrMovieMissing() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    assertTrue(movieService.updateMovie("t", null, "Tom").isEmpty());
    when(movieRepository.findByWatchTime("t")).thenReturn(Optional.empty());
    assertTrue(movieService.updateMovie("t", new Movie(), "Tom").isEmpty());
  }

  @Test
  void addAndRemoveImage_returnEmptyWhenBadArgsOrMovieMissing() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    assertTrue(movieService.addImage("t", null, "Tom").isEmpty());
    assertTrue(movieService.addImage("t", " ", "Tom").isEmpty());

    when(movieRepository.findByWatchTime("t")).thenReturn(Optional.empty());
    assertTrue(movieService.addImage("t", "a.png", "Tom").isEmpty());

    assertTrue(movieService.removeImage("t", null, "Tom").isEmpty());
    assertTrue(movieService.removeImage("t", " ", "Tom").isEmpty());
  }

  @Test
  void listImages_returnsEmptyWhenMovieMissingOrNotOwner() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    when(movieRepository.findByWatchTime("t")).thenReturn(Optional.empty());
    assertTrue(movieService.listImages("t", "Tom").isEmpty());

    User owner = new User();
    owner.setName("Owner");
    Movie movie = new Movie();
    movie.setUser(owner);
    movie.getImages().add("a.png");
    when(movieRepository.findByWatchTime("t2")).thenReturn(Optional.of(movie));
    assertTrue(movieService.listImages("t2", "Other").isEmpty());
    assertEquals(List.of("a.png"), movieService.listImages("t2", "Owner"));
  }

  @Test
  void findDtoByWatchTime_returnsEmptyOnBlank() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    assertTrue(movieService.findDtoByWatchTime(null).isEmpty());
    assertTrue(movieService.findDtoByWatchTime(" ").isEmpty());
    verifyNoInteractions(movieRepository);
  }
}
