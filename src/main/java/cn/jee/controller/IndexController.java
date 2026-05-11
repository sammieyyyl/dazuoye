package cn.jee.controller;

import cn.jee.web.Views;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class IndexController {
  @RequestMapping({"/", "/index"})
  public String index() {
    return Views.INDEX;
  }
}
