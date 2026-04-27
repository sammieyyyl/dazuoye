package cn.jee.controller;

import cn.jee.entity.Movie;
import cn.jee.repository.MovieRepository;
import cn.jee.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

@Controller
@RequestMapping("/movie")
public class MovieController {
  @Autowired
  MovieRepository movieRepository;

  @Autowired
  UserRepository userRepository;

  @RequestMapping("/list")
  public String list(Model model, HttpSession session) {
    Object loginUser = session.getAttribute("login_user");
    if (loginUser == null) {
      return "redirect:/";
    }
    String username = loginUser.toString();
    var movies = movieRepository.findAllByUser_NameOrderByWatchTimeDesc(username);
    model.addAttribute("movies", movies);
    model.addAttribute("username", username);
    return "movie/list";
  }

  @RequestMapping("/new")
  public String newPage(Model model, HttpSession session) {
    Object loginUser = session.getAttribute("login_user");
    if (loginUser == null) {
      return "redirect:/";
    }
    model.addAttribute("movie", new Movie());
    return "movie/new";
  }

  @RequestMapping(value = "/save", method = RequestMethod.POST)
  public String save(@Valid Movie movie, BindingResult bindingResult, HttpSession session) {
    Object loginUser = session.getAttribute("login_user");
    if (loginUser == null) {
      return "redirect:/";
    }
    if (bindingResult.hasErrors()) {
      return "movie/new";
    }
    String username = loginUser.toString();
    var userOpt = userRepository.findByName(username);
    if (userOpt.isEmpty()) {
      return "redirect:/";
    }
    movie.setUser(userOpt.get());
    movieRepository.save(movie);
    return "redirect:/movie/list";
  }

  @RequestMapping("/upload_page")
  public String uploadPage(String watchTime, Model model, HttpSession session) {
    Object loginUser = session.getAttribute("login_user");
    if (loginUser == null) {
      return "redirect:/";
    }
    model.addAttribute("watchTime", watchTime);
    return "movie/upload";
  }

  @RequestMapping(value = "/upload", method = RequestMethod.POST)
  public String upload(String watchTime, MultipartFile file, HttpSession session) throws IOException {
    Object loginUser = session.getAttribute("login_user");
    if (loginUser == null) {
      return "redirect:/";
    }
    var movieOpt = movieRepository.findByWatchTime(watchTime);
    if (movieOpt.isEmpty()) {
      return "redirect:/movie/list";
    }

    String fileName = file == null ? null : file.getOriginalFilename();
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

    return "redirect:/movie/list";
  }
}