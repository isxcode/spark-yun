package com.isxcode.star.common.tools;

import com.isxcode.star.backend.api.base.exceptions.IsxAppException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
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

@Tag(name = "工具模块")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tools")
@Slf4j
public class ToolController {

    private final CacheManager cacheManager;

    private final ResourceLoader resourceLoader;

    private final SecurityProperties securityProperties;

    @Value("${jasypt.encryptor.password}")
    private String jasyptPassword;

    @Operation(summary = "获取版本号接口")
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

    @Operation(summary = "切换系统日志等级")
    @GetMapping("/open/setLogLevel")
    public String setLogLevel(@RequestParam String level, @RequestParam String name, @RequestParam String password) {

        if (securityProperties.getUser().getName().equals(name)
            && securityProperties.getUser().getPassword().equals(password)) {
            LoggingSystem system = LoggingSystem.get(LoggingSystem.class.getClassLoader());
            LogLevel logLevel = LogLevel.valueOf(level.trim().toUpperCase(Locale.ENGLISH));
            system.setLogLevel("com.isxcode.acorn", logLevel);
            return "当前日志等级：" + logLevel.name();
        } else {
            return "没有权限";
        }
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

    @Operation(summary = "返回当前应用状态")
    @GetMapping("/open/health")
    public String health() {

        return "UP";
    }

    @Operation(summary = "jasypt加密工具")
    @GetMapping("/open/jasyptEncrypt")
    public String jasypt(@RequestParam String text) {

        AES256TextEncryptor encryptor = new AES256TextEncryptor();
        encryptor.setPassword(jasyptPassword);
        return encryptor.encrypt(text);
    }
}
