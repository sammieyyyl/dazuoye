package cn.jee.controller;

import cn.jee.service.MovieService;
import cn.jee.service.UploadService;
import cn.jee.service.UserService;
import cn.jee.web.Redirects;
import cn.jee.web.Views;
import cn.jee.web.dto.MovieDto;
import cn.jee.web.form.MovieUpdateForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/movie")
public class MovieCrudController {
  private final MovieService movieService;
  private final UploadService uploadService;
  private final UserService userService;

  public MovieCrudController(MovieService movieService, UploadService uploadService, UserService userService) {
    this.movieService = movieService;
    this.uploadService = uploadService;
    this.userService = userService;
  }

  @RequestMapping("/detail")
  public String detail(String watchTime, Model model, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    var movieOpt = movieService.findDtoByWatchTime(watchTime);
    if (movieOpt.isEmpty()) {
      return Redirects.MOVIE_LIST;
    }
    MovieDto movie = movieOpt.get();
    model.addAttribute("movie", movie);
    model.addAttribute("username", usernameOpt.get());
    model.addAttribute("images", imageUrls(movie.images()));
    return Views.MOVIE_DETAIL;
  }

  @RequestMapping("/edit")
  public String edit(String watchTime, Model model, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    var movieOpt = movieService.findDtoByWatchTime(watchTime);
    if (movieOpt.isEmpty()) {
      return Redirects.MOVIE_LIST;
    }
    MovieDto movie = movieOpt.get();
    MovieUpdateForm form = new MovieUpdateForm();
    form.setName(movie.name());
    form.setPrice(movie.price());
    form.setComment(movie.comment());
    model.addAttribute("watchTime", watchTime);
    model.addAttribute("form", form);
    model.addAttribute("images", imageUrls(movie.images()));
    return Views.MOVIE_EDIT;
  }

  @PostMapping("/update")
  public String update(String watchTime, @Valid MovieUpdateForm form, BindingResult bindingResult, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    if (bindingResult.hasErrors()) {
      return Redirects.MOVIE_LIST;
    }
    movieService.updateMovie(watchTime, form.toUpdates(), usernameOpt.get());
    return Redirects.MOVIE_LIST;
  }

  @PostMapping("/delete")
  public String delete(String watchTime, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    movieService.deleteMovie(watchTime, usernameOpt.get());
    return Redirects.MOVIE_LIST;
  }

  @PostMapping("/remove_image")
  public String removeImage(String watchTime, String imageName, HttpSession session) throws IOException {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    movieService.removeImage(watchTime, imageName, usernameOpt.get());
    uploadService.deleteFromUploads(imageName);
    return Redirects.MOVIE_LIST;
  }

  @PostMapping("/upload_to_movie")
  public String uploadToMovie(String watchTime, MultipartFile file, HttpSession session) throws IOException {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return Redirects.ROOT;
    }
    String storedName = uploadService.saveToUploads(file);
    movieService.addImage(watchTime, storedName, usernameOpt.get());
    return Redirects.MOVIE_LIST;
  }

  private List<String> imageUrls(List<String> images) {
    if (images == null || images.isEmpty()) {
      return List.of();
    }
    return images.stream()
      .map(i -> "/uploads/" + i)
      .toList();
  }
}
