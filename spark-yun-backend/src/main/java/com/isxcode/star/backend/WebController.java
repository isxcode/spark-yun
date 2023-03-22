package com.isxcode.star.backend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
public class WebController {

  @RequestMapping(value = {"/*"})
  public String index() {

    return "index";
  }
}
