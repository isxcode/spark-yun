package com.isxcode.star.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/** 系统入口. */
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

  @RequestMapping(value = {"/user"})
  public String user() {

    return "index";
  }

  @RequestMapping(value = {"/tenant"})
  public String tenant() {

    return "index";
  }

  @RequestMapping(value = {"/tenant_user"})
  public String tenantUser() {

    return "index";
  }

  @RequestMapping(value = {"/license"})
  public String license() {

    return "index";
  }

  @RequestMapping(value = {"/engine"})
  public String engine() {

    return "index";
  }

  @RequestMapping(value = {"/datasource"})
  public String datasource() {

    return "index";
  }

  @RequestMapping(value = {"/workflow"})
  public String workflow() {

    return "index";
  }

}
