package com.isxcode.star.backend;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/** 前端入口. */
@Controller
@RequestMapping(value = "/")
public class WebController {

  @RequestMapping(value = {"/*"})
  public String index() {

    return "index";
  }
}
