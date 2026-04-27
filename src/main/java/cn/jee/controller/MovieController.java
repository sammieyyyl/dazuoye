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
}