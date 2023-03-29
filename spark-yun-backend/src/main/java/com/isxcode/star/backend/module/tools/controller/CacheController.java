package com.isxcode.star.backend.module.tools.controller;

import java.util.Collection;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** 缓存相关工具接口. */
@RestController
@RequestMapping("/cache")
@RequiredArgsConstructor
public class CacheController {

  private final CacheManager cacheManager;

  /** 获取本地缓存. */
  @GetMapping("/getCache")
  public void getCache() {
    Collection<String> cacheNames = cacheManager.getCacheNames();
    for (String cacheName : cacheNames) {
      Cache cache = cacheManager.getCache(cacheName);
      Object nativeCache = cache.getNativeCache();
      System.out.println("Cache " + cacheName + " content: " + nativeCache);
    }
  }
}
