package cn.jee.web.form;

import cn.jee.entity.Movie;

public class MovieUpdateForm extends MovieBaseForm {
  public Movie toUpdates() {
    Movie movie = new Movie();
    movie.setName(getName());
    movie.setPrice(getPrice());
    movie.setComment(getComment());
    return movie;
  }
}
