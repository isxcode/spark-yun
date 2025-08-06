package com.isxcode.spark.common.tools;

import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.*;
import java.util.Locale;
import java.util.Objects;

@Tag(name = "系统内部工具模块")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tools")
@Slf4j
public class ToolController {

    private final CacheManager cacheManager;

    private final ResourceLoader resourceLoader;

    @Value("${jasypt.encryptor.password}")
    private String jasyptPassword;

    @Value("${jasypt.encryptor.property.suffix}")
    private String jasyptSuffix;

    @Value("${jasypt.encryptor.property.prefix}")
    private String jasyptPrefix;

    @Operation(summary = "获取当前版本信息接口")
    @GetMapping("/open/version")
    public String getLeoLastVersion() {

        try {
            Resource resource = resourceLoader.getResource("classpath:VERSION");
            InputStream inputStream = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder content = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
            reader.close();
            return content.toString();
        } catch (IOException e) {
            log.error(e.getMessage());
            throw new IsxAppException("获取版本号异常", e.getMessage());
        }
    }

    @Operation(summary = "健康检查接口")
    @GetMapping("/open/health")
    public String health() {

        return "UP";
    }

    @Operation(summary = "健康检查接口")
    @GetMapping("/open/file")
    public String file() {

        return "UP";
    }

    @Operation(summary = "切换系统日志等级接口")
    @GetMapping("/developer/setLogLevel")
    public String setLogLevel(@RequestParam String level) {

        LoggingSystem system = LoggingSystem.get(LoggingSystem.class.getClassLoader());
        LogLevel logLevel = LogLevel.valueOf(level.trim().toUpperCase(Locale.ENGLISH));
        system.setLogLevel("com.isxcode.acorn", logLevel);
        return "当前日志等级：" + logLevel.name();
    }

    @Operation(summary = "获取缓存列表接口")
    @GetMapping("/developer/getCacheList")
    public String getCacheList() {

        return cacheManager.getCacheNames().toString();
    }

    @Operation(summary = "获取指定缓存信息接口")
    @GetMapping("/developer/getCache")
    public String getCache(@RequestParam String name) {

        return Objects.requireNonNull(cacheManager.getCache(name)).getNativeCache().toString();
    }

    @Operation(summary = "jasypt加密工具接口")
    @GetMapping("/developer/jasyptEncrypt")
    public String jasypt(@RequestParam String text) {

        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        encryptor.setPassword(jasyptPassword);
        return "加密后配置参数：" + jasyptPrefix + encryptor.encrypt(text) + jasyptSuffix;
    }
}
