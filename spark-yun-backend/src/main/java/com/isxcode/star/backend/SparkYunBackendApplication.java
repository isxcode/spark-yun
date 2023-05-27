package com.isxcode.star.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/")
@SpringBootApplication
public class SparkYunBackendApplication {

  public static void main(String[] args) {
    SpringApplication.run(SparkYunBackendApplication.class, args);
  }

  @RequestMapping(value = {"/*"})
  public String index() {

    return "index";
  }

  @RequestMapping(value = {"/works/{workflowId}"})
  public String works(@PathVariable String workflowId) {

    return "index";
  }

  @RequestMapping(value = {"/work/{workId}"})
  public String work(@PathVariable String workId) {

    return "index";
  }

  @RequestMapping(value = {"/nodes/{clusterId}"})
  public String nodes(@PathVariable String clusterId) {

    return "index";
  }
}
