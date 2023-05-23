package com.isxcode.star.backend.module.user.action;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.isxcode.star.api.properties.SparkYunProperties;
import com.isxcode.star.api.exceptions.SuccessResponseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

import java.util.Enumeration;

import static com.isxcode.star.backend.config.WebSecurityConfig.TENANT_ID;
import static com.isxcode.star.backend.config.WebSecurityConfig.USER_ID;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class UserLogAdvice {

  private final UserActionRepository userActionRepository;

  private UserActionEntity userActionEntity;

  private final SparkYunProperties sparkYunProperties;

  @Pointcut("@annotation(com.isxcode.star.backend.module.user.action.UserLog)")
  public void operateUserLog() {
  }

  @Before(value = "operateUserLog()")
  public void before(JoinPoint joinPoint) {

    if (!sparkYunProperties.isLogAdvice()) {
      return;
    }

    userActionEntity = new UserActionEntity();
    userActionEntity.setStartTimestamp(System.currentTimeMillis());
    if (Strings.isEmpty(USER_ID.get())) {
      userActionEntity.setUserId("anonymous");
    } else {
      userActionEntity.setUserId(USER_ID.get());
    }

    if (Strings.isEmpty(TENANT_ID.get())) {
      userActionEntity.setTenantId("anonymous");
    } else {
      userActionEntity.setTenantId(TENANT_ID.get());
    }

    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();

    userActionEntity.setReqPath(request.getRequestURI());

    userActionEntity.setReqMethod(request.getMethod());

    JSONObject jsonObject = new JSONObject();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String name = headerNames.nextElement();
      String value = request.getHeader(name);
      jsonObject.put(name, value);
    }
    userActionEntity.setReqHeader(jsonObject.toJSONString());

    if ("GET".equals(request.getMethod())) {
      userActionEntity.setReqBody(request.getQueryString());
    }

    if ("POST".equals(request.getMethod())) {
      if (joinPoint.getArgs().length > 0) {
        userActionEntity.setReqBody(JSON.toJSONString(joinPoint.getArgs()[0]));
      }
    }
  }

  @After(value = "operateUserLog()")
  public void after(JoinPoint joinPoint) {

    if (!sparkYunProperties.isLogAdvice()) {
      return;
    }

    if (Strings.isEmpty(USER_ID.get())) {
      userActionEntity.setCreateBy("anonymous");
    }
    userActionEntity.setEndTimestamp(System.currentTimeMillis());
    try {
      userActionRepository.save(userActionEntity);
    } catch (Exception e) {
      log.error(e.getMessage());
    }
  }

  @AfterThrowing(value = "operateUserLog()", throwing = "successResponseException")
  public void afterThrowing(JoinPoint joinPoint, SuccessResponseException successResponseException) {

    if (!sparkYunProperties.isLogAdvice()) {
      return;
    }

    userActionEntity.setResBody(JSON.toJSONString(successResponseException.getBaseResponse()));
  }
}
