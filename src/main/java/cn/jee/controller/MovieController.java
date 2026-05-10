package cn.jee.controller;

import cn.jee.entity.Movie;
import cn.jee.repository.MovieRepository;
import cn.jee.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/movie")
public class MovieController {
  private static final String SESSION_LOGIN_USER = "login_user";
  private static final String REDIRECT_ROOT = "redirect:/";
  private static final String REDIRECT_MOVIE_LIST = "redirect:/movie/list";

  private final MovieRepository movieRepository;

  private final UserRepository userRepository;

  public MovieController(MovieRepository movieRepository, UserRepository userRepository) {
    this.movieRepository = movieRepository;
    this.userRepository = userRepository;
  }

  @RequestMapping("/list")
  public String list(Model model, HttpSession session) {
    Object loginUser = session.getAttribute(SESSION_LOGIN_USER);
    if (loginUser == null) {
      return REDIRECT_ROOT;
    }
    String username = loginUser.toString();
    var movies = movieRepository.findAllByUser_NameOrderByWatchTimeDesc(username);
    model.addAttribute("movies", movies);
    model.addAttribute("username", username);
    return "movie/list";
  }

  @RequestMapping("/new")
  public String newPage(Model model, HttpSession session) {
    Object loginUser = session.getAttribute(SESSION_LOGIN_USER);
    if (loginUser == null) {
      return REDIRECT_ROOT;
    }
    model.addAttribute("movie", new Movie());
    return "movie/new";
  }

  @PostMapping("/save")
  public String save(@Valid Movie movie, BindingResult bindingResult, HttpSession session) {
    Object loginUser = session.getAttribute(SESSION_LOGIN_USER);
    if (loginUser == null) {
      return REDIRECT_ROOT;
    }
    if (bindingResult.hasErrors()) {
      return "movie/new";
    }
    String username = loginUser.toString();
    var userOpt = userRepository.findByName(username);
    if (userOpt.isEmpty()) {
      return REDIRECT_ROOT;
    }
    movie.setUser(userOpt.get());
    movieRepository.save(movie);
    return REDIRECT_MOVIE_LIST;
  }

  @RequestMapping("/upload_page")
  public String uploadPage(String watchTime, Model model, HttpSession session) {
    Object loginUser = session.getAttribute(SESSION_LOGIN_USER);
    if (loginUser == null) {
      return REDIRECT_ROOT;
    }
    model.addAttribute("watchTime", watchTime);
    return "movie/upload";
  }

  @PostMapping("/upload")
  public String upload(String watchTime, MultipartFile file, HttpSession session) throws IOException {
    Object loginUser = session.getAttribute(SESSION_LOGIN_USER);
    if (loginUser == null) {
      return REDIRECT_ROOT;
    }
    var movieOpt = movieRepository.findByWatchTime(watchTime);
    if (movieOpt.isEmpty()) {
      return REDIRECT_MOVIE_LIST;
    }

    if (file == null || file.isEmpty()) {
      throw new IOException("请选择文件");
    }

    String fileName = file.getOriginalFilename();
    if (fileName == null || fileName.isBlank()) {
      fileName = "file";
    }

    Path uploadDir = Paths.get("uploads");
    Files.createDirectories(uploadDir);
    Path dest = uploadDir.resolve(fileName);
    file.transferTo(dest);

    Movie movie = movieOpt.get();
    if (movie.getImages() == null) {
      movie.setImages(new ArrayList<>());
    }
    movie.getImages().add(fileName);
    movieRepository.save(movie);

    return REDIRECT_MOVIE_LIST;
  }

  @ResponseBody
  @RequestMapping("/images")
  public List<String> images(String watchTime, HttpSession session) {
    Object loginUser = session.getAttribute(SESSION_LOGIN_USER);
    if (loginUser == null) {
      return List.of();
    }
    var movieOpt = movieRepository.findByWatchTime(watchTime);
    if (movieOpt.isEmpty()) {
      return List.of();
    }
    Movie movie = movieOpt.get();
    if (movie.getImages() == null) {
      return List.of();
    }
    return movie.getImages();
  }
}
