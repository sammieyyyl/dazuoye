package cn.jee;

import cn.jee.entity.Movie;
import cn.jee.entity.User;
import cn.jee.repository.MovieRepository;
import cn.jee.repository.UserRepository;
import cn.jee.service.MovieService;
import cn.jee.web.dto.MovieDto;
import cn.jee.web.form.MovieCreateForm;
import cn.jee.web.form.MovieUpdateForm;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class MovieServiceTest {

  @Test
  void listByUsername_delegatesToRepository() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    when(movieRepository.findAllByUser_NameOrderByWatchTimeDesc("Tom")).thenReturn(List.of());

    List<Movie> movies = movieService.listByUsername("Tom");
    assertNotNull(movies);
    verify(movieRepository).findAllByUser_NameOrderByWatchTimeDesc("Tom");
  }

  @Test
  void createMovie_setsUserAndSaves() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    User user = new User();
    user.setName("Tom");
    when(userRepository.findByName("Tom")).thenReturn(Optional.of(user));
    when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Movie movie = new Movie();
    movie.setWatchTime("2025010101");
    movie.setName("M");
    movie.setPrice(10.0);
    movie.setComment("comment comment comment");

    Optional<Movie> created = movieService.createMovie(movie, "Tom");
    assertTrue(created.isPresent());
    assertEquals("Tom", created.get().getUser().getName());
    verify(movieRepository).save(any(Movie.class));
  }

  @Test
  void updateMovie_updatesFieldsForOwner() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    User user = new User();
    user.setName("Tom");

    Movie existing = new Movie();
    existing.setWatchTime("2025010101");
    existing.setName("Old");
    existing.setPrice(1.0);
    existing.setComment("old comment old comment old");
    existing.setUser(user);

    when(movieRepository.findByWatchTime("2025010101")).thenReturn(Optional.of(existing));
    when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Movie updates = new Movie();
    updates.setName("New");
    updates.setPrice(2.0);
    updates.setComment("new comment new comment new");

    Optional<Movie> updated = movieService.updateMovie("2025010101", updates, "Tom");
    assertTrue(updated.isPresent());
    assertEquals("New", updated.get().getName());
    assertEquals(2.0, updated.get().getPrice());
    verify(movieRepository).save(any(Movie.class));
  }

  @Test
  void deleteMovie_deletesOnlyForOwner() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    User user = new User();
    user.setName("Tom");
    Movie existing = new Movie();
    existing.setWatchTime("2025010101");
    existing.setUser(user);

    when(movieRepository.findByWatchTime("2025010101")).thenReturn(Optional.of(existing));

    boolean deleted = movieService.deleteMovie("2025010101", "Tom");
    assertTrue(deleted);
    verify(movieRepository).delete(existing);
  }

  @Test
  void addAndRemoveImage_updatesImagesForOwner() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    User user = new User();
    user.setName("Tom");
    Movie existing = new Movie();
    existing.setWatchTime("2025010101");
    existing.setUser(user);

    when(movieRepository.findByWatchTime("2025010101")).thenReturn(Optional.of(existing));
    when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Optional<Movie> added = movieService.addImage("2025010101", "a.png", "Tom");
    assertTrue(added.isPresent());
    assertTrue(added.get().getImages().contains("a.png"));

    Optional<Movie> removed = movieService.removeImage("2025010101", "a.png", "Tom");
    assertTrue(removed.isPresent());
    assertFalse(removed.get().getImages().contains("a.png"));
  }

  @Test
  void listDtosByUsername_returnsEmptyWhenBlank() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    assertTrue(movieService.listDtosByUsername(null).isEmpty());
    assertTrue(movieService.listDtosByUsername(" ").isEmpty());
    verifyNoInteractions(movieRepository);
  }

  @Test
  void listDtosByUsername_mapsMovies() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    User user = new User();
    user.setName("Tom");
    Movie movie = new Movie();
    movie.setWatchTime("2025010101");
    movie.setName("M");
    movie.setPrice(1.0);
    movie.setComment("comment comment comment");
    movie.setUser(user);

    when(movieRepository.findAllByUser_NameOrderByWatchTimeDesc("Tom")).thenReturn(List.of(movie));

    List<MovieDto> dtos = movieService.listDtosByUsername("Tom");
    assertEquals(1, dtos.size());
    assertEquals("2025010101", dtos.get(0).watchTime());
    assertEquals("Tom", dtos.get(0).username());
  }

  @Test
  void createMovieDto_buildsEntityFromFormAndReturnsDto() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    User user = new User();
    user.setName("Tom");
    when(userRepository.findByName("Tom")).thenReturn(Optional.of(user));
    when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

    MovieCreateForm form = new MovieCreateForm();
    form.setWatchTime("2025010101");
    form.setName("M");
    form.setPrice(1.0);
    form.setComment("comment comment comment");

    Optional<MovieDto> created = movieService.createMovieDto(form, "Tom");
    assertTrue(created.isPresent());
    assertEquals("2025010101", created.get().watchTime());
    assertEquals("Tom", created.get().username());
  }

  @Test
  void updateMovieDto_updatesAndReturnsDto() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    User user = new User();
    user.setName("Tom");
    Movie existing = new Movie();
    existing.setWatchTime("t");
    existing.setName("Old");
    existing.setPrice(1.0);
    existing.setComment("old comment old comment old");
    existing.setUser(user);

    when(movieRepository.findByWatchTime("t")).thenReturn(Optional.of(existing));
    when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

    MovieUpdateForm form = new MovieUpdateForm();
    form.setName("New");
    form.setPrice(2.0);
    form.setComment("new comment new comment new");

    Optional<MovieDto> updated = movieService.updateMovieDto("t", form, "Tom");
    assertTrue(updated.isPresent());
    assertEquals("New", updated.get().name());
    assertEquals(2.0, updated.get().price());
  }

  @Test
  void addImageDto_and_listImages_workForOwner() {
    MovieRepository movieRepository = mock(MovieRepository.class);
    UserRepository userRepository = mock(UserRepository.class);
    MovieService movieService = new MovieService(movieRepository, userRepository, null);

    User user = new User();
    user.setName("Tom");
    Movie existing = new Movie();
    existing.setWatchTime("t");
    existing.setUser(user);

    when(movieRepository.findByWatchTime("t")).thenReturn(Optional.of(existing));
    when(movieRepository.save(any(Movie.class))).thenAnswer(invocation -> invocation.getArgument(0));

    Optional<MovieDto> added = movieService.addImageDto("t", "a.png", "Tom");
    assertTrue(added.isPresent());
    assertTrue(added.get().images().contains("a.png"));

    assertEquals(List.of("a.png"), movieService.listImages("t", "Tom"));
    assertTrue(movieService.listImages("t", "Other").isEmpty());
  }
}
