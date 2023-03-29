package com.isxcode.star.backend.module.tools.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 版本查询模块. */
@RestController
@RequestMapping
public class VersionController {

  @GetMapping("/getVersion")
  public String getVersion() {

    return "v0.0.1";
  }
}
