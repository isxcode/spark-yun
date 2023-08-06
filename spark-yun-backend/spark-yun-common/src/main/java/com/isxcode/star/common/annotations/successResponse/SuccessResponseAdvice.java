package com.isxcode.star.common.annotations.successResponse;

import com.isxcode.star.backend.api.base.exceptions.SuccessResponseException;
import com.isxcode.star.backend.api.base.pojos.BaseResponse;
import java.io.InputStream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;

@Aspect
@Slf4j
@RequiredArgsConstructor
public class SuccessResponseAdvice {

  private final MessageSource messageSource;

  @Pointcut("@annotation(com.isxcode.star.common.annotations.successResponse.SuccessResponse)")
  public void operateLog() {}

  @AfterReturning(returning = "data", value = "operateLog()&&@annotation(successResponse)")
  public void afterReturning(JoinPoint joinPoint, Object data, SuccessResponse successResponse) {

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    BaseResponse<Object> baseResponse = new BaseResponse<>();
    if (!"void".equals(signature.getReturnType().getName())) {
      if (data instanceof InputStream) {
        return;
      }
      baseResponse.setCode(String.valueOf(HttpStatus.OK.value()));
      if (data.getClass().getDeclaredFields().length == 0) {
        baseResponse.setData(null);
      } else {
        baseResponse.setData(data);
      }
      baseResponse.setMsg(getMsg(successResponse));
      successResponse(baseResponse);
    } else {
      baseResponse.setCode(String.valueOf(HttpStatus.OK.value()));
      baseResponse.setMsg(getMsg(successResponse));
      successResponse(baseResponse);
    }
  }

  public String getMsg(SuccessResponse successResponse) {

    if (!successResponse.value().isEmpty()) {
      return successResponse.value();
    }

    try {
      return messageSource.getMessage(successResponse.msg(), null, LocaleContextHolder.getLocale());
    } catch (NoSuchMessageException e) {
      return successResponse.msg();
    }
  }

  public void successResponse(BaseResponse<Object> baseResponse) {

    throw new SuccessResponseException(baseResponse);
  }
}
