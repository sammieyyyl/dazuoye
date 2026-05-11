package cn.jee.controller.api;

import cn.jee.service.MovieService;
import cn.jee.service.UploadService;
import cn.jee.service.UserService;
import cn.jee.web.dto.ApiResponse;
import cn.jee.web.dto.MovieDto;
import cn.jee.web.form.MovieCreateForm;
import cn.jee.web.form.MovieUpdateForm;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/movie")
public class MovieApiController {
  private final MovieService movieService;
  private final UploadService uploadService;
  private final UserService userService;

  public MovieApiController(MovieService movieService, UploadService uploadService, UserService userService) {
    this.movieService = movieService;
    this.uploadService = uploadService;
    this.userService = userService;
  }

  @GetMapping("/list")
  public Map<String, Object> list(HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return ApiResponse.fail("未登录");
    }
    List<MovieDto> movies = movieService.listByUsername(usernameOpt.get())
      .stream()
      .map(MovieDto::from)
      .toList();
    return ApiResponse.ok("ok", movies);
  }

  @GetMapping("/detail")
  public Map<String, Object> detail(String watchTime, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return ApiResponse.fail("未登录");
    }
    var movieOpt = movieService.findByWatchTime(watchTime);
    if (movieOpt.isEmpty()) {
      return ApiResponse.fail("未找到影片");
    }
    MovieDto dto = MovieDto.from(movieOpt.get());
    return ApiResponse.ok("ok", dto);
  }

  @PostMapping("/create")
  public Map<String, Object> create(@Valid MovieCreateForm form, BindingResult bindingResult, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return ApiResponse.fail("未登录");
    }
    if (bindingResult.hasErrors()) {
      return ApiResponse.fail("参数不合法", bindingResult.getAllErrors());
    }
    var created = movieService.createMovie(form.toEntity(), usernameOpt.get());
    if (created.isEmpty()) {
      return ApiResponse.fail("创建失败");
    }
    return ApiResponse.ok("ok", MovieDto.from(created.get()));
  }

  @PostMapping("/update")
  public Map<String, Object> update(String watchTime, @Valid MovieUpdateForm form, BindingResult bindingResult, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return ApiResponse.fail("未登录");
    }
    if (bindingResult.hasErrors()) {
      return ApiResponse.fail("参数不合法", bindingResult.getAllErrors());
    }
    var updated = movieService.updateMovie(watchTime, form.toUpdates(), usernameOpt.get());
    if (updated.isEmpty()) {
      return ApiResponse.fail("更新失败");
    }
    return ApiResponse.ok("ok", MovieDto.from(updated.get()));
  }

  @PostMapping("/delete")
  public Map<String, Object> delete(String watchTime, HttpSession session) {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return ApiResponse.fail("未登录");
    }
    boolean deleted = movieService.deleteMovie(watchTime, usernameOpt.get());
    if (!deleted) {
      return ApiResponse.fail("删除失败");
    }
    return ApiResponse.ok("ok");
  }

  @PostMapping("/upload")
  public Map<String, Object> upload(String watchTime, MultipartFile file, HttpSession session) throws IOException {
    var usernameOpt = userService.getLoginUsername(session);
    if (usernameOpt.isEmpty()) {
      return ApiResponse.fail("未登录");
    }
    String stored = uploadService.saveToUploads(file);
    var movieOpt = movieService.addImage(watchTime, stored, usernameOpt.get());
    if (movieOpt.isEmpty()) {
      uploadService.deleteFromUploads(stored);
      return ApiResponse.fail("上传失败");
    }
    return ApiResponse.ok("ok", MovieDto.from(movieOpt.get()));
  }
}
