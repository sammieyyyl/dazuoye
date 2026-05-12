package cn.jee.controller;

import cn.jee.service.MovieService;
import cn.jee.service.UploadService;
import cn.jee.service.UserService;
import cn.jee.web.Redirects;
import cn.jee.web.Views;
import cn.jee.web.form.MovieCreateForm;
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
import java.util.List;

@Controller
@RequestMapping("/movie")
public class MovieController {
  private final MovieService movieService;
  private final UploadService uploadService;
  private final UserService userService;

  public MovieController(MovieService movieService, UploadService uploadService, UserService userService) {
    this.movieService = movieService;
    this.uploadService = uploadService;
    this.userService = userService;
  }

  @RequestMapping("/list")
  public String list(Model model, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    String username = usernameOpt.get();
    var movies = movieService.listDtosByUsername(username);
    model.addAttribute("movies", movies);
    model.addAttribute("username", username);
    return Views.MOVIE_LIST;
  }

  @RequestMapping("/new")
  public String newPage(Model model, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    model.addAttribute("form", new MovieCreateForm());
    return Views.MOVIE_NEW;
  }

  @PostMapping("/save")
  public String save(@Valid MovieCreateForm form, BindingResult bindingResult, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    if (bindingResult.hasErrors()) {
      return Views.MOVIE_NEW;
    }
    String username = usernameOpt.get();
    if (movieService.createMovieDto(form, username).isEmpty()) {
      return Redirects.ROOT;
    }
    return Redirects.MOVIE_LIST;
  }

  @RequestMapping("/upload_page")
  public String uploadPage(String watchTime, Model model, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    model.addAttribute("watchTime", watchTime);
    return Views.MOVIE_UPLOAD;
  }

  @PostMapping("/upload")
  public String upload(String watchTime, MultipartFile file, HttpSession session) throws IOException {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    if (file == null || file.isEmpty()) {
      throw new IOException("请选择文件");
    }
    String username = usernameOpt.get();
    String stored = uploadService.saveToUploads(file);
    if (movieService.addImage(watchTime, stored, username).isEmpty()) {
      uploadService.deleteFromUploads(stored);
    }

    return Redirects.MOVIE_LIST;
  }

  @ResponseBody
  @RequestMapping("/images")
  public List<String> images(String watchTime, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return List.of();
    }
    return movieService.listImages(watchTime, usernameOpt.get());
  }
}
