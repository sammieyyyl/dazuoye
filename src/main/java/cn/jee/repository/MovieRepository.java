package cn.jee.repository;

import cn.jee.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
  Optional<Movie> findByWatchTime(String watchTime);

  List<Movie> findAllByUser_NameOrderByWatchTimeDesc(String name);
}