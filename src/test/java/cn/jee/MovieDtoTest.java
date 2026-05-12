package cn.jee;

import cn.jee.entity.Movie;
import cn.jee.entity.User;
import cn.jee.web.dto.MovieDto;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieDtoTest {

  @Test
  void from_handlesNullAndCopiesFields() {
    assertNull(MovieDto.from(null));

    User user = new User();
    user.setName("Tom");

    Movie movie = new Movie();
    movie.setWatchTime("2025010101");
    movie.setName("M");
    movie.setPrice(12.0);
    movie.setComment("comment comment comment");
    movie.setUser(user);
    movie.getImages().addAll(List.of("a.png", "b.png"));

    MovieDto dto = MovieDto.from(movie);
    assertEquals("2025010101", dto.watchTime());
    assertEquals("Tom", dto.username());
    assertEquals(2, dto.images().size());
  }

  @Test
  void movie_equalsAndHashCode_useWatchTime() {
    Movie a = new Movie();
    a.setWatchTime("2025010101");
    Movie b = new Movie();
    b.setWatchTime("2025010101");
    Movie c = new Movie();
    c.setWatchTime("2025010102");

    assertEquals(a, a);
    assertNotNull(a);
    assertNotEquals("x", a);
    assertEquals(a, b);
    assertNotEquals(a, c);
    assertEquals(b.hashCode(), a.hashCode());
  }
}
