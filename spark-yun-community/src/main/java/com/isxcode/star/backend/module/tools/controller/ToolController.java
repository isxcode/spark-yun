package com.isxcode.star.backend.module.tools.controller;

import com.isxcode.star.api.exception.SparkYunException;
import com.isxcode.star.backend.module.user.action.annoation.UserLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Tag(name = "工具模块")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tools")
@Slf4j
public class ToolController {

  private final CacheManager cacheManager;

  @UserLog
  @Operation(summary = "获取版本号接口")
  @GetMapping("/open/version")
  public String getLeoLastVersion() {

    File file = new File("./VERSION");
    FileInputStream fis;
    try {
      fis = new FileInputStream(file);
      BufferedReader br = new BufferedReader(new InputStreamReader(fis));
      String version = br.readLine();
      br.close();
      fis.close();
      return version;
    } catch (IOException e) {
      log.error(e.getMessage());
      throw new SparkYunException("获取版本号异常", e.getMessage());
    }
  }

  @UserLog
  @Operation(summary = "获取缓存接口")
  @GetMapping("/open/getCache")
  public Map<String, String> getCache() {

    Map<String, String> cacheMap = new HashMap<>();

    Collection<String> cacheNames = cacheManager.getCacheNames();
    for (String cacheName : cacheNames) {
      cacheMap.put(cacheName, String.valueOf(cacheManager.getCache(cacheName).getNativeCache()));
    }

    return cacheMap;
  }
}

