package com.isxcode.star.modules.version;

import com.isxcode.star.backend.api.base.exceptions.SparkYunException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "工具模块")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tools")
@Slf4j
public class ToolController {

  private final CacheManager cacheManager;

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
}
