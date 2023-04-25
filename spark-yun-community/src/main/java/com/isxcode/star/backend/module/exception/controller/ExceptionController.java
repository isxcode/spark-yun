package com.isxcode.star.backend.module.exception.controller;

import com.isxcode.star.api.exception.SparkYunException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Token异常模块")
@RestController
@RequestMapping("/exception")
public class ExceptionController {

  @Operation(summary = "token为null异常")
  @RequestMapping(path = "/tokenIsNull", method = {RequestMethod.GET, RequestMethod.POST})
  public void tokenIsNull() {

    throw new SparkYunException("token为null异常");
  }

  @Operation(summary = "token不合法异常")
  @RequestMapping(path = "/tokenIsInvalid", method = {RequestMethod.GET, RequestMethod.POST})
  public void tokenIsInvalid() {

    throw new SparkYunException("token不合法异常");
  }

  @Operation(summary = "权限不足异常")
  @RequestMapping(path = "/authError", method = {RequestMethod.GET, RequestMethod.POST})
  public void exceptionAuthError() {

    throw new SparkYunException("权限不足异常");
  }

  @Operation(summary = "证书无效接口")
  @RequestMapping(path = "/licenseError", method = {RequestMethod.GET, RequestMethod.POST})
  public void licenseError() {

    throw new SparkYunException("证书无效");
  }
}
