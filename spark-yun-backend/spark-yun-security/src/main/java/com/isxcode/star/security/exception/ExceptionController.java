package com.isxcode.star.security.exception;

import com.isxcode.star.backend.api.base.exceptions.SparkYunException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Token异常模块")
@RestController
@RequestMapping("/exception")
public class ExceptionController {

  @Operation(summary = "token为null异常")
  @RequestMapping(
      path = "/tokenIsNull",
      method = {RequestMethod.GET, RequestMethod.POST})
  public void tokenIsNull() {

    throw new SparkYunException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "token为null异常");
  }

  @Operation(summary = "token不合法异常")
  @RequestMapping(
      path = "/tokenIsInvalid",
      method = {RequestMethod.GET, RequestMethod.POST})
  public void tokenIsInvalid() {

    throw new SparkYunException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "token异常，请重新登录");
  }

  @Operation(summary = "权限不足异常")
  @RequestMapping(
      path = "/authError",
      method = {RequestMethod.GET, RequestMethod.POST})
  public void exceptionAuthError() {

    throw new SparkYunException(String.valueOf(HttpStatus.FORBIDDEN.value()), "权限不足异常");
  }

  @Operation(summary = "证书无效接口")
  @RequestMapping(
      path = "/licenseError",
      method = {RequestMethod.GET, RequestMethod.POST})
  public void licenseError() {

    throw new SparkYunException(String.valueOf(HttpStatus.FORBIDDEN.value()), "许可证无效，请联系管理员");
  }
}
