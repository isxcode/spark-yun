package com.isxcode.star.security.exception;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "系统内部异常模块")
@RestController
@RequestMapping("/exception")
public class ExceptionController {

    @Operation(summary = "token不存在异常")
    @RequestMapping(path = "/tokenIsNull", method = {RequestMethod.GET, RequestMethod.POST})
    public void tokenIsNull() {

        throw new IsxAppException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "token异常，请重新登录");
    }

    @Operation(summary = "token不合法异常")
    @RequestMapping(path = "/tokenIsInvalid", method = {RequestMethod.GET, RequestMethod.POST})
    public void tokenIsInvalid() {

        throw new IsxAppException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "token异常，请重新登录");
    }

    @Operation(summary = "暂无权限异常")
    @RequestMapping(path = "/authError", method = {RequestMethod.GET, RequestMethod.POST})
    public void exceptionAuthError() {

        throw new IsxAppException(String.valueOf(HttpStatus.FORBIDDEN.value()), "暂无权限");
    }

    @Operation(summary = "许可证无效异常")
    @RequestMapping(path = "/licenseError", method = {RequestMethod.GET, RequestMethod.POST})
    public void licenseError() {

        throw new IsxAppException(String.valueOf(HttpStatus.FORBIDDEN.value()), "许可证无效，请联系管理员");
    }
}
