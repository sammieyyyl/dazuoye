package cn.jee.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "movie")
public class Movie {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank(message = "{movie.watchTime.notBlank}")
  @Size(min = 10, max = 10, message = "{movie.watchTime.size}")
  @Column(unique = true, nullable = false, length = 10)
  private String watchTime;

  private String name;

  @NotNull(message = "{movie.price.positive}")
  @Positive(message = "{movie.price.positive}")
  private Double price;

  @NotNull(message = "{movie.comment.min}")
  @Size(min = 20, message = "{movie.comment.min}")
  private String comment;

  @ElementCollection
  @CollectionTable(name = "movie_image", joinColumns = @JoinColumn(name = "movie_id"))
  @Column(name = "image_name")
  private List<String> images = new ArrayList<>();

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Movie movie = (Movie) o;
    return Objects.equals(watchTime, movie.watchTime);
  }

  @Override
  public int hashCode() {
    return Objects.hash(watchTime);
  }
}