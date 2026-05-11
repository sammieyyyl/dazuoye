package cn.jee.service;

import cn.jee.entity.Movie;
import cn.jee.repository.MovieRepository;
import cn.jee.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class MovieService {
  private final MovieRepository movieRepository;
  private final UserRepository userRepository;

  public MovieService(MovieRepository movieRepository, UserRepository userRepository) {
    this.movieRepository = movieRepository;
    this.userRepository = userRepository;
  }

  public List<Movie> listByUsername(String username) {
    return movieRepository.findAllByUser_NameOrderByWatchTimeDesc(username);
  }

  public Optional<Movie> findByWatchTime(String watchTime) {
    if (watchTime == null || watchTime.isBlank()) {
      return Optional.empty();
    }
    return movieRepository.findByWatchTime(watchTime);
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
