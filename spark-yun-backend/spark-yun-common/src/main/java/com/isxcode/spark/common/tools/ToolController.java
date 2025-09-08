package com.isxcode.spark.common.tools;

import com.isxcode.spark.backend.api.base.exceptions.IsxAppException;
import com.isxcode.spark.backend.api.base.properties.IsxAppProperties;
import com.isxcode.spark.common.utils.path.PathUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.logging.LogLevel;
import org.springframework.boot.logging.LoggingSystem;
import org.springframework.cache.CacheManager;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.Objects;

import static java.net.URLConnection.guessContentTypeFromName;


@Tag(name = "系统内部工具模块")
@RestController
@RequiredArgsConstructor
@RequestMapping("/tools")
@Slf4j
public class ToolController {

    private final CacheManager cacheManager;

    private final ResourceLoader resourceLoader;
    private final IsxAppProperties isxAppProperties;

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
    @GetMapping("/open/file/{fileName}")
    public ResponseEntity<Resource> file(@PathVariable String fileName) {

        if (Strings.isEmpty(isxAppProperties.getOpenFilePath()) || Strings.isEmpty(fileName)) {
            return ResponseEntity.notFound().build();
        }

        try {
            InputStreamResource resource = new InputStreamResource(Files.newInputStream(
                Paths.get(PathUtils.parseProjectPath(isxAppProperties.getOpenFilePath()) + File.separator + fileName)));
            HttpHeaders headers = new HttpHeaders();
            String contentTypeFromName = guessContentTypeFromName(fileName.toLowerCase());
            if (contentTypeFromName == null) {
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            } else {
                headers.setContentType(MediaType.parseMediaType(contentTypeFromName));
            }

            // 检查文件类型并设置相应的处理方式
            String lowerFileName = fileName.toLowerCase();

            // 设置Content-Disposition
            if (lowerFileName.endsWith(".pdf")) {
                headers.add("Content-Disposition", "inline; filename=\"" + URLEncoder.encode(fileName, "UTF-8") + "\"");
            } else {
                headers.setContentDispositionFormData("attachment", URLEncoder.encode(fileName, "UTF-8"));
            }

            // 设置缓存控制：对于PDF、tar.gz、lic文件禁用缓存，确保每次都获取最新版本
            if (lowerFileName.endsWith(".pdf") || lowerFileName.endsWith(".tar.gz") || lowerFileName.endsWith(".lic")) {
                headers.setCacheControl("no-cache, no-store, must-revalidate");
                headers.add("Expires", "0");
            } else {
                // 其他文件保持长期缓存
                headers.setCacheControl("public, max-age=" + 31536000);
            }

            // 返回文件
            return ResponseEntity.ok().headers(headers).body(resource);
        } catch (IOException e) {
            log.debug(e.getMessage(), e);
            return ResponseEntity.notFound().build();
        }
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
