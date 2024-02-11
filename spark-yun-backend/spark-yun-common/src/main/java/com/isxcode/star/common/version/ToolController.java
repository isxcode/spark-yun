package com.isxcode.star.common.version;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.Locale;
import java.util.Objects;

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
			throw new IsxAppException("获取版本号异常", e.getMessage());
		}
	}

	@Operation(summary = "切换系统日志等级")
	@GetMapping("/setLogLevel")
	public String setLogLevel(@RequestParam String level) {

		LoggingSystem system = LoggingSystem.get(LoggingSystem.class.getClassLoader());
		LogLevel logLevel = LogLevel.valueOf(level.trim().toUpperCase(Locale.ENGLISH));
		system.setLogLevel("com.isxcode.star", logLevel);
		return "当前日志等级：" + logLevel.name();
	}

	@Operation(summary = "获取缓存列表")
	@GetMapping("/getCacheList")
	public String getCacheList() {

		return cacheManager.getCacheNames().toString();
	}

	@Operation(summary = "获取制定缓存信息")
	@GetMapping("/getCache")
	public String getCache(@RequestParam String name) {

		return Objects.requireNonNull(cacheManager.getCache(name)).getNativeCache().toString();
	}

}
