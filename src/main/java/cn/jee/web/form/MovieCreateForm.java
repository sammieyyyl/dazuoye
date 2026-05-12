package cn.jee.web.form;

import cn.jee.entity.Movie;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class MovieCreateForm extends MovieBaseForm {
  @NotBlank
  @Size(min = 10, max = 10)
  private String watchTime;

  public Movie toEntity() {
    Movie movie = new Movie();
    movie.setWatchTime(watchTime);
    movie.setName(getName());
    movie.setPrice(getPrice());
    movie.setComment(getComment());
    return movie;
  }

  public String getWatchTime() {
    return watchTime;
  }

  public void setWatchTime(String watchTime) {
    this.watchTime = watchTime;
  }
}
