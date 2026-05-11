package cn.jee.web.dto;

import cn.jee.entity.Movie;

import java.util.List;

public record MovieDto(
  String watchTime,
  String name,
  Double price,
  String comment,
  String username,
  List<String> images
) {
  public static MovieDto from(Movie movie) {
    if (movie == null) {
      return null;
    }
    String username = movie.getUser() == null ? null : movie.getUser().getName();
    List<String> images = movie.getImages() == null ? List.of() : List.copyOf(movie.getImages());
    return new MovieDto(
      movie.getWatchTime(),
      movie.getName(),
      movie.getPrice(),
      movie.getComment(),
      username,
      images
    );
  }
}
