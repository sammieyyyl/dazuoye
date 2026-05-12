package cn.jee.service;

import cn.jee.entity.Movie;
import cn.jee.repository.MovieRepository;
import cn.jee.repository.UserRepository;
import cn.jee.web.dto.MovieDto;
import cn.jee.web.form.MovieCreateForm;
import cn.jee.web.form.MovieUpdateForm;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
  private final MovieRepository movieRepository;
  private final UserRepository userRepository;
  private final MovieService self;

  public MovieService(MovieRepository movieRepository, UserRepository userRepository, @Lazy MovieService self) {
    this.movieRepository = movieRepository;
    this.userRepository = userRepository;
    this.self = self == null ? this : self;
  }

  public List<Movie> listByUsername(String username) {
    return movieRepository.findAllByUser_NameOrderByWatchTimeDesc(username);
  }

  public List<MovieDto> listDtosByUsername(String username) {
    if (username == null || username.isBlank()) {
      return List.of();
    }
    return listByUsername(username).stream()
      .map(MovieDto::from)
      .toList();
  }

  public Optional<Movie> findByWatchTime(String watchTime) {
    if (watchTime == null || watchTime.isBlank()) {
      return Optional.empty();
    }
    return movieRepository.findByWatchTime(watchTime);
  }

  public Optional<MovieDto> findDtoByWatchTime(String watchTime) {
    return findByWatchTime(watchTime).map(MovieDto::from);
  }

  @Transactional
  public Optional<Movie> createMovie(Movie movie, String username) {
    if (movie == null) {
      return Optional.empty();
    }
    if (username == null || username.isBlank()) {
      return Optional.empty();
    }
    var userOpt = userRepository.findByName(username);
    if (userOpt.isEmpty()) {
      return Optional.empty();
    }
    movie.setUser(userOpt.get());
    return Optional.of(movieRepository.save(movie));
  }

  @Transactional
  public Optional<MovieDto> createMovieDto(MovieCreateForm form, String username) {
    if (form == null) {
      return Optional.empty();
    }
    return self.createMovie(form.toEntity(), username).map(MovieDto::from);
  }

  @Transactional
  public Optional<Movie> updateMovie(String watchTime, Movie updates, String username) {
    if (updates == null) {
      return Optional.empty();
    }
    var movieOpt = movieRepository.findByWatchTime(watchTime);
    if (movieOpt.isEmpty()) {
      return Optional.empty();
    }
    Movie movie = movieOpt.get();
    if (!isOwner(movie, username)) {
      return Optional.empty();
    }
    if (updates.getName() != null) {
      movie.setName(updates.getName());
    }
    if (updates.getPrice() != null) {
      movie.setPrice(updates.getPrice());
    }
    if (updates.getComment() != null) {
      movie.setComment(updates.getComment());
    }
    return Optional.of(movieRepository.save(movie));
  }

  @Transactional
  public Optional<MovieDto> updateMovieDto(String watchTime, MovieUpdateForm form, String username) {
    if (form == null) {
      return Optional.empty();
    }
    return self.updateMovie(watchTime, form.toUpdates(), username).map(MovieDto::from);
  }

  @Transactional
  public boolean deleteMovie(String watchTime, String username) {
    var movieOpt = movieRepository.findByWatchTime(watchTime);
    if (movieOpt.isEmpty()) {
      return false;
    }
    Movie movie = movieOpt.get();
    if (!isOwner(movie, username)) {
      return false;
    }
    movieRepository.delete(movie);
    return true;
  }

  @Transactional
  public Optional<Movie> addImage(String watchTime, String imageName, String username) {
    if (imageName == null || imageName.isBlank()) {
      return Optional.empty();
    }
    var movieOpt = movieRepository.findByWatchTime(watchTime);
    if (movieOpt.isEmpty()) {
      return Optional.empty();
    }
    Movie movie = movieOpt.get();
    if (!isOwner(movie, username)) {
      return Optional.empty();
    }
    movie.getImages().add(imageName);
    return Optional.of(movieRepository.save(movie));
  }

  @Transactional
  public Optional<MovieDto> addImageDto(String watchTime, String imageName, String username) {
    return self.addImage(watchTime, imageName, username).map(MovieDto::from);
  }

  @Transactional
  public Optional<Movie> removeImage(String watchTime, String imageName, String username) {
    if (imageName == null || imageName.isBlank()) {
      return Optional.empty();
    }
    var movieOpt = movieRepository.findByWatchTime(watchTime);
    if (movieOpt.isEmpty()) {
      return Optional.empty();
    }
    Movie movie = movieOpt.get();
    if (!isOwner(movie, username)) {
      return Optional.empty();
    }
    movie.getImages().removeIf(imageName::equals);
    return Optional.of(movieRepository.save(movie));
  }

  public List<String> listImages(String watchTime, String username) {
    var movieOpt = movieRepository.findByWatchTime(watchTime);
    if (movieOpt.isEmpty()) {
      return List.of();
    }
    Movie movie = movieOpt.get();
    if (!isOwner(movie, username)) {
      return List.of();
    }
    return List.copyOf(movie.getImages());
  }

  private boolean isOwner(Movie movie, String username) {
    if (movie.getUser() == null) {
      return false;
    }
    if (username == null || username.isBlank()) {
      return false;
    }
    return username.equals(movie.getUser().getName());
  }
}
