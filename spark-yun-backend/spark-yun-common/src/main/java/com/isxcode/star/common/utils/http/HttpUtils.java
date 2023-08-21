package com.isxcode.star.common.utils.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

/** 网络请求工具类. */
@Slf4j
public class HttpUtils {

  public static <A> A doGet(
      String url,
      Map<String, String> requestParams,
      Map<String, String> headerParams,
      Class<A> targetClass) {

    StringBuilder requestUrl = new StringBuilder(url);

    // add params
    if (requestParams != null) {
      requestUrl.append("?");
      requestParams.forEach((k, v) -> requestUrl.append(k).append("=").append(v).append("&"));
    }

    // add headers
    HttpEntity<String> requestEntity = null;
    if (headerParams != null) {
      HttpHeaders headers = new HttpHeaders();
      headerParams.forEach(headers::add);
      requestEntity = new HttpEntity<>(null, headers);
    }

    return new RestTemplate()
        .exchange(requestUrl.toString(), HttpMethod.GET, requestEntity, targetClass)
        .getBody();
  }

  public static <A> A doGet(String url, Class<A> targetClass) {

    return doGet(url, null, null, targetClass);
  }

  public static <A> A doGet(String url, Map<String, String> headerParams, Class<A> targetClass) {

    return doGet(url, null, headerParams, targetClass);
  }

  public static <T> T doPost(
      String url, Map<String, String> headerParams, Object requestParams, Class<T> targetCls)
      throws IOException {

    HttpHeaders headers = new HttpHeaders();

    if (headerParams == null || headerParams.get(HttpHeaders.CONTENT_TYPE) == null) {
      headers.add(HttpHeaders.CONTENT_TYPE, "application/json;charset=UTF-8");
    }

    if (headerParams != null) {
      headerParams.forEach(headers::add);
    }

    HttpEntity<String> requestEntity =
        new HttpEntity<>(new ObjectMapper().writeValueAsString(requestParams), headers);
    return new RestTemplate().exchange(url, HttpMethod.POST, requestEntity, targetCls).getBody();
  }

  public static <T> T doPost(String url, Object requestParams, Class<T> targetCls)
      throws IOException {

    return doPost(url, null, requestParams, targetCls);
  }

  public static String doPost(String url, Map<String, String> headerParams, Object requestParams)
      throws IOException {

    return doPost(url, headerParams, requestParams, String.class);
  }
}
