package cn.jee.controller;

import cn.jee.repository.MovieRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/movie")
public class MovieController {
  @Autowired
  MovieRepository movieRepository;

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
}