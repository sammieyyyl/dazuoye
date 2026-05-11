package cn.jee.web.form;

import cn.jee.entity.Movie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class MovieCreateForm {
  @NotBlank
  @Size(min = 10, max = 10)
  private String watchTime;

  @NotBlank
  @Size(max = 200)
  private String name;

  @NotNull
  @Positive
  private Double price;

  @NotBlank
  @Size(min = 20, max = 1000)
  private String comment;

  public Movie toEntity() {
    Movie movie = new Movie();
    movie.setWatchTime(watchTime);
    movie.setName(name);
    movie.setPrice(price);
    movie.setComment(comment);
    return movie;
  }

  public String getWatchTime() {
    return watchTime;
  }

  public void setWatchTime(String watchTime) {
    this.watchTime = watchTime;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Double getPrice() {
    return price;
  }

  public void setPrice(Double price) {
    this.price = price;
  }

  public String getComment() {
    return comment;
  }

  public void setComment(String comment) {
    this.comment = comment;
  }
}
